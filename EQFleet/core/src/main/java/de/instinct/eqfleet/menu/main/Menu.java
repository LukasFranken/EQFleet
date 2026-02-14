package de.instinct.eqfleet.menu.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.meta.dto.modules.ModuleInfoRequest;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.module.construction.Construction;
import de.instinct.eqfleet.menu.module.construction.ConstructionRenderer;
import de.instinct.eqfleet.menu.module.construction.message.ReloadConstructionMessage;
import de.instinct.eqfleet.menu.module.core.ModuleManager;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import de.instinct.eqfleet.menu.module.play.Play;
import de.instinct.eqfleet.menu.module.play.PlayModel;
import de.instinct.eqfleet.menu.module.play.PlayRenderer;
import de.instinct.eqfleet.menu.module.play.message.UpdateLobbyStatusMessage;
import de.instinct.eqfleet.menu.module.profile.Profile;
import de.instinct.eqfleet.menu.module.profile.ProfileRenderer;
import de.instinct.eqfleet.menu.module.profile.message.LoadProfileMessage;
import de.instinct.eqfleet.menu.module.settings.Settings;
import de.instinct.eqfleet.menu.module.settings.SettingsRenderer;
import de.instinct.eqfleet.menu.module.ship.Shipyard;
import de.instinct.eqfleet.menu.module.ship.ShipyardRenderer;
import de.instinct.eqfleet.menu.module.ship.message.ReloadShipyardMessage;
import de.instinct.eqfleet.menu.module.shop.Shop;
import de.instinct.eqfleet.menu.module.shop.ShopRenderer;
import de.instinct.eqfleet.menu.module.starmap.Starmap;
import de.instinct.eqfleet.menu.module.starmap.StarmapRenderer;
import de.instinct.eqfleet.menu.postgame.PostGameModel;
import de.instinct.eqfleet.menu.postgame.PostGameRenderer;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;

public class Menu {
	
	private static PostGameRenderer postGameRenderer;
	private static MenuRenderer menuRenderer;
	private static GridRenderer gridRenderer;
	
	private static ScheduledExecutorService scheduler;
	private static long UPDATE_CLOCK_MS = 20;
	
	private static Queue<ModuleMessage> moduleMessageQueue;
	private static Queue<MenuModule> reloadRequired;
	
	public static void init() {
		ModuleManager.init();
		postGameRenderer = new PostGameRenderer();
		menuRenderer = new MenuRenderer();
		
		gridRenderer = new GridRenderer(GridConfiguration.builder()
				.step(10f)
				.build());
		
		reloadRequired = new ConcurrentLinkedQueue<>();
		
		MenuModel.modules = new HashMap<>();
		MenuModel.renderers = new LinkedHashMap<>();
		
		MenuModel.active = false;
		
		MenuModel.renderers.put(MenuModule.PROFILE, new ProfileRenderer());
		MenuModel.renderers.put(MenuModule.SETTINGS, new SettingsRenderer());
		MenuModel.renderers.put(MenuModule.STARMAP, new StarmapRenderer());
		MenuModel.renderers.put(MenuModule.SHIPYARD, new ShipyardRenderer());
		MenuModel.renderers.put(MenuModule.CONSTRUCTION, new ConstructionRenderer());
		MenuModel.renderers.put(MenuModule.SHOP, new ShopRenderer());
		MenuModel.renderers.put(MenuModule.PLAY, new PlayRenderer());
		
		MenuModel.modules.put(MenuModule.PLAY, new Play());
		MenuModel.modules.put(MenuModule.PROFILE, new Profile());
		MenuModel.modules.put(MenuModule.SETTINGS, new Settings());
		MenuModel.modules.put(MenuModule.SHIPYARD, new Shipyard());
		MenuModel.modules.put(MenuModule.CONSTRUCTION, new Construction());
		MenuModel.modules.put(MenuModule.SHOP, new Shop());
		MenuModel.modules.put(MenuModule.STARMAP, new Starmap());
		
		for (BaseModule module : MenuModel.modules.values()) {
			module.init();
		}
		
		moduleMessageQueue = new ConcurrentLinkedQueue<>();
		
		MenuModel.loaded = false;
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			try {
				update();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 0, UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
	}
	
	private static void calculateMenuBounds() {
		float margin = 20f;
		MenuModel.moduleBounds = new Rectangle(margin, margin + 20, GraphicsUtil.screenBounds().width - (margin * 2), GraphicsUtil.screenBounds().height - 150 - 40f);
	}
	
	public static void open() {
		Logger.log("MENU", "Opening Menu with last game UUID: " + GameModel.lastGameUUID, ConsoleColor.YELLOW);
		if (GameModel.lastGameUUID == null || GameModel.lastGameUUID.contentEquals("custom") || GameModel.lastGameUUID.contentEquals("tutorial")) {
			load();
			MenuModel.active = true;
		} else {
			WebManager.enqueue(
				    () -> API.matchmaking().result(GameModel.lastGameUUID),
				    result -> {
				    	if (result == null) {
				    		Logger.log("Menu", "Failed to load post game data for UUID " + GameModel.lastGameUUID, ConsoleColor.RED);
					    	load();
					    	MenuModel.active = true;
				    	} else {
				    		PostGameModel.reward = result;
					    	Gdx.app.postRunnable(() -> {
					    		postGameRenderer.reload();
					    		MenuModel.active = true;
					    	});
				    	}
				    },
				    error -> {
				    	Logger.log("Menu", "Failed to load post game data for UUID " + GameModel.lastGameUUID + " with error: " + error.getMessage(), ConsoleColor.RED);
				    	load();
				    	MenuModel.active = true;
				    }
			);
		}
	}
	
	public static void load() {
		MenuModel.loaded = false;
		calculateMenuBounds();
		updateBaseInfo();
		loadModules();
		if (PlayModel.lobbyStatus != null && PlayModel.lobbyStatus.getType().getGameMode() == GameMode.CONQUEST) Play.leaveLobby();
	}

	private static void updateBaseInfo() {
		queue(ReloadShipyardMessage.builder().build());
		queue(ReloadConstructionMessage.builder().build());
		if (PostGameModel.reward == null) {
			queue(LoadProfileMessage.builder().build());
			queue(UpdateLobbyStatusMessage.builder().build());
		}
	}

	public static void close() {
		MenuModel.active = false;
		MenuModel.activeModule = null;
		menuRenderer.close();
	}
	
	private static void update() {
		while (!moduleMessageQueue.isEmpty()) {
			ModuleMessage message = moduleMessageQueue.poll();
			BaseModule module = MenuModel.modules.get(message.getMenuModule());
			if (module == null) continue;
			module.processBackendMessage(message);
		}
		for (BaseModule module : MenuModel.modules.values()) {
			if (MenuModel.unlockedModules != null && MenuModel.unlockedModules.getEnabledModules().contains(module.getMenuModule())) {
				module.update();
			}
		}
	}
	
	public static void render() {
		while (reloadRequired.peek() != null) {
			MenuModel.renderers.get(reloadRequired.poll()).reload();
		}
		if (MenuModel.active) {
			if (GlobalStaticData.showDebugGrid) gridRenderer.drawGrid();
			if (PostGameModel.reward != null) {
				postGameRenderer.render();
			} else {
				if (MenuModel.activeModule != null) {
					MenuModel.renderers.get(MenuModel.activeModule).render();
				}
				menuRenderer.render();
			}
		}
	}
	
	public static void queue(ModuleMessage message) {
		moduleMessageQueue.add(message);
	}
	
	public static void openModule(MenuModule menuModule) {
		BaseModule module = MenuModel.modules.get(menuModule);
		if (module == null) {
			Logger.log("Menu", "Tried to open missing module: " + menuModule);
			return;
		}
		module.open();
		MenuModel.activeModule = menuModule;
		ModuleManager.openModule(menuModule);
	}
	
	public static void closeModule() {
		MenuModel.activeModule = null;
		updateBaseInfo();
	}
	
	public static void loadModules() {
		WebManager.enqueue(
			    () -> API.meta().modules(API.authKey),
			    modulesResult -> {
			    	if (modulesResult != null) {
			    		MenuModel.unlockedModules = modulesResult;
			    		List<MenuModule> lockedModules = new ArrayList<>();
			    		for (MenuModule module : MenuModule.values()) {
			    			if (!MenuModel.unlockedModules.getEnabledModules().contains(module)) {
			    				lockedModules.add(module);
			    			}
			    		}
			    		loadLockedModuleInfo(lockedModules);
			    		loadButtons();
			    		reloadAll();
					} else {
						Logger.log("Menu", "ModuleData couldn't be loaded!", ConsoleColor.RED);
					}
			    }
		);
	}
	
	private static void loadButtons() {
		MenuModel.buttons = new ArrayList<>();
		if (MenuModel.unlockedModules != null && MenuModel.unlockedModules.getEnabledModules() != null) {
	        for (MenuModule module : MenuModel.renderers.keySet()) {
	            if (MenuModel.unlockedModules.getEnabledModules().contains(module)) {
	                MenuModel.buttons.add(module);
	            }
	        }
	    }
	}

	private static void loadLockedModuleInfo(List<MenuModule> lockedModules) {
		ModuleInfoRequest moduleInfoRequest = new ModuleInfoRequest();
		moduleInfoRequest.setRequestedModuleInfos(lockedModules);
		WebManager.enqueue(
			    () -> API.meta().moduleInfo(moduleInfoRequest),
			    moduleInfoResult -> {
			    	if (moduleInfoResult != null) {
			    		MenuModel.lockedModules = moduleInfoResult;
					} else {
						Logger.log("Menu", "Locked ModuleData couldn't be loaded!", ConsoleColor.RED);
					}
			    }
		);
	}
	
	public static void reloadContent() {
		Gdx.app.postRunnable(() -> {
			menuRenderer.reloadContent();
		});
	}

	public static void reloadAll() {
		Gdx.app.postRunnable(() -> {
			menuRenderer.reload();
			for (BaseModuleRenderer renderer : MenuModel.renderers.values()) {
				renderer.reload();
			}
			MenuModel.loaded = true;
		});
	}
	
	public static void requireReload(MenuModule module) {
		reloadRequired.add(module);
	}

	public static void dispose() {
		postGameRenderer.dispose();
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
		close();
		menuRenderer.dispose();
		for (BaseModule module : MenuModel.modules.values()) {
			if (module != null) {
				module.close();
			}
		}
		for (BaseModuleRenderer renderer : MenuModel.renderers.values()) {
			if (renderer != null) {
				renderer.dispose();
			}
		}
	}

}
