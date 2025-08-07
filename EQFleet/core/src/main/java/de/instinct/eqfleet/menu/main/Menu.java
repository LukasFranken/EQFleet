package de.instinct.eqfleet.menu.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
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
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.module.construction.Construction;
import de.instinct.eqfleet.menu.module.construction.ConstructionRenderer;
import de.instinct.eqfleet.menu.module.construction.message.ReloadConstructionMessage;
import de.instinct.eqfleet.menu.module.core.ModuleManager;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import de.instinct.eqfleet.menu.module.inventory.Inventory;
import de.instinct.eqfleet.menu.module.inventory.InventoryRenderer;
import de.instinct.eqfleet.menu.module.inventory.message.LoadResourcesMessage;
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
import de.instinct.eqfleet.menu.module.starmap.message.ReloadStarmapMessage;
import de.instinct.eqfleet.menu.module.workshop.Workshop;
import de.instinct.eqfleet.menu.module.workshop.WorkshopRenderer;
import de.instinct.eqfleet.menu.postgame.PostGameModel;
import de.instinct.eqfleet.menu.postgame.PostGameRenderer;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class Menu {
	
	private static PostGameRenderer postGameRenderer;
	private static MenuRenderer menuRenderer;
	
	private static Map<MenuModule, BaseModule> modules;
	private static Map<MenuModule, BaseModuleRenderer> renderers;
	
	private static ScheduledExecutorService scheduler;
	private static long UPDATE_CLOCK_MS = 20;
	
	private static Queue<ModuleMessage> moduleMessageQueue;
	private static Queue<MenuModule> reloadRequired;
	
	public static void init() {
		ModuleManager.init();
		postGameRenderer = new PostGameRenderer();
		menuRenderer = new MenuRenderer();
		reloadRequired = new ConcurrentLinkedQueue<>();
		
		modules = new HashMap<>();
		renderers = new HashMap<>();
		
		MenuModel.active = false;
		
		modules.put(MenuModule.PLAY, new Play());
		renderers.put(MenuModule.PLAY, new PlayRenderer());
		modules.put(MenuModule.PROFILE, new Profile());
		renderers.put(MenuModule.PROFILE, new ProfileRenderer());
		modules.put(MenuModule.INVENTORY, new Inventory());
		renderers.put(MenuModule.INVENTORY, new InventoryRenderer());
		modules.put(MenuModule.SETTINGS, new Settings());
		renderers.put(MenuModule.SETTINGS, new SettingsRenderer());
		modules.put(MenuModule.SHIPYARD, new Shipyard());
		renderers.put(MenuModule.SHIPYARD, new ShipyardRenderer());
		modules.put(MenuModule.CONSTRUCTION, new Construction());
		renderers.put(MenuModule.CONSTRUCTION, new ConstructionRenderer());
		modules.put(MenuModule.WORKSHOP, new Workshop());
		renderers.put(MenuModule.WORKSHOP, new WorkshopRenderer());
		modules.put(MenuModule.SHOP, new Shop());
		renderers.put(MenuModule.SHOP, new ShopRenderer());
		modules.put(MenuModule.STARMAP, new Starmap());
		renderers.put(MenuModule.STARMAP, new StarmapRenderer());
		
		for (BaseModule module : modules.values()) {
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
	
	public static void load() {
		MenuModel.loaded = false;
		calculateMenuBounds();
		updateBaseInfo();
		loadModules();
	}
	
	private static void calculateMenuBounds() {
		float margin = 20f;
		MenuModel.moduleBounds = new Rectangle(margin, margin + 20, GraphicsUtil.baseScreenBounds().width - (margin * 2), GraphicsUtil.baseScreenBounds().height - 150 - 40f);
	}

	
	public static void loadPostGame() {
		if (PlayModel.lobbyStatus.getType().getGameMode() == GameMode.CONQUEST) {
			queue(ReloadStarmapMessage.builder().build());
		}
		WebManager.enqueue(
			    () -> API.matchmaking().result(GameModel.activeGameState.gameUUID),
			    result -> {
			    	PostGameModel.reward = result;
			    	Gdx.app.postRunnable(() -> {
			    		postGameRenderer.reload();
			    		Menu.load();
			    	});
			    }
		);
	}
	
	public static void open() {
		Game.dispose();
		MenuModel.active = true;
		AudioManager.playMusic("eqspace" + (new Random().nextInt(4) + 1), true);
	}

	private static void updateBaseInfo() {
		queue(ReloadShipyardMessage.builder().build());
		queue(ReloadConstructionMessage.builder().build());
		if (PostGameModel.reward == null) {
			queue(LoadProfileMessage.builder().build());
			queue(LoadResourcesMessage.builder().build());
			queue(UpdateLobbyStatusMessage.builder().build());
		}
	}

	public static void close() {
		MenuModel.active = false;
		menuRenderer.close();
	}
	
	private static void update() {
		while (!moduleMessageQueue.isEmpty()) {
			ModuleMessage message = moduleMessageQueue.poll();
			BaseModule module = modules.get(message.getMenuModule());
			if (module == null) continue;
			module.processBackendMessage(message);
		}
		for (BaseModule module : modules.values()) {
			if (MenuModel.unlockedModules != null && MenuModel.unlockedModules.getEnabledModules().contains(module.getMenuModule())) {
				module.update();
			}
		}
	}
	
	public static void render() {
		while (reloadRequired.peek() != null) {
			renderers.get(reloadRequired.poll()).reload();
		}
		if (MenuModel.active) {
			if (PostGameModel.reward != null) {
				postGameRenderer.render();
			} else {
				if (MenuModel.activeModule != null) {
					renderers.get(MenuModel.activeModule).render();
				}
				menuRenderer.render();
			}
		}
	}
	
	public static void queue(ModuleMessage message) {
		moduleMessageQueue.add(message);
	}
	
	public static void openModule(MenuModule menuModule) {
		BaseModule module = modules.get(menuModule);
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
			    		ModuleInfoRequest moduleInfoRequest = new ModuleInfoRequest();
			    		moduleInfoRequest.setRequestedModuleInfos(lockedModules);
			    		WebManager.enqueue(
			    			    () -> API.meta().moduleInfo(moduleInfoRequest),
			    			    moduleInfoResult -> {
			    			    	if (moduleInfoResult != null) {
			    			    		MenuModel.lockedModules = moduleInfoResult;
			    			    		reload();
			    					} else {
			    						Logger.log("Menu", "Locked ModuleData couldn't be loaded!", ConsoleColor.RED);
			    					}
			    			    }
			    		);
					} else {
						Logger.log("Menu", "ModuleData couldn't be loaded!", ConsoleColor.RED);
					}
			    }
		);
	}
	
	public static void reload() {
		Gdx.app.postRunnable(() -> {
			menuRenderer.reload();
			for (BaseModuleRenderer renderer : renderers.values()) {
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
		for (BaseModule module : modules.values()) {
			if (module != null) {
				module.close();
			}
		}
		for (BaseModuleRenderer renderer : renderers.values()) {
			if (renderer != null) {
				renderer.dispose();
			}
		}
	}

}
