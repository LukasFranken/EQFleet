package de.instinct.eqfleet.menu.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.dto.LobbyStatusCode;
import de.instinct.api.meta.dto.modules.ModuleInfoRequest;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.module.construction.Construction;
import de.instinct.eqfleet.menu.module.construction.ConstructionRenderer;
import de.instinct.eqfleet.menu.module.inventory.Inventory;
import de.instinct.eqfleet.menu.module.inventory.InventoryRenderer;
import de.instinct.eqfleet.menu.module.inventory.message.LoadResourcesMessage;
import de.instinct.eqfleet.menu.module.main.tab.play.PlayTab;
import de.instinct.eqfleet.menu.module.play.Play;
import de.instinct.eqfleet.menu.module.play.PlayRenderer;
import de.instinct.eqfleet.menu.module.profile.Profile;
import de.instinct.eqfleet.menu.module.profile.ProfileRenderer;
import de.instinct.eqfleet.menu.module.profile.message.LoadProfileMessage;
import de.instinct.eqfleet.menu.module.settings.Settings;
import de.instinct.eqfleet.menu.module.settings.SettingsRenderer;
import de.instinct.eqfleet.menu.module.ship.Shipyard;
import de.instinct.eqfleet.menu.module.ship.ShipyardRenderer;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class Menu {
	
	private static MenuRenderer menuRenderer;
	
	private static Map<MenuModule, BaseModule> modules;
	private static Map<MenuModule, BaseModuleRenderer> renderers;
	
	private static ScheduledExecutorService scheduler;
	private static long UPDATE_CLOCK_MS = 20;
	
	private static Queue<ModuleMessage> moduleMessageQueue;
	private static Queue<MenuModule> reloadRequired;
	
	public static void init() {
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
		
		moduleMessageQueue = new ConcurrentLinkedQueue<>();
	}
	
	public static void open() {
		Game.dispose();
		loadModules();
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			update();
		}, 0, UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
		MenuModel.active = true;
		PlayTab.init();
		PlayTab.loadData();
	}
	
	public static void close() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
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
		if (MenuModel.moduleChanged) {
			reload();
			MenuModel.moduleChanged = false;
		}
		while (reloadRequired.peek() != null) {
			renderers.get(reloadRequired.poll()).reload();
		}
		if (MenuModel.active) {
			if (MenuModel.activeModule != null) {
				renderers.get(MenuModel.activeModule).render();
			}
			menuRenderer.render();
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
	}
	
	public static void closeModule() {
		MenuModel.activeModule = null;
	}
	
	public static void loadModules() {
		WebManager.enqueue(
			    () -> API.meta().modules(API.authKey),
			    result -> {
			    	if (result != null) {
			    		MenuModel.unlockedModules = result;
			    		
			    		List<MenuModule> lockedModules = new ArrayList<>();
			    		for (MenuModule module : MenuModule.values()) {
			    			if (!MenuModel.unlockedModules.getEnabledModules().contains(module)) {
			    				lockedModules.add(module);
			    			}
			    		}
			    		WebManager.enqueue(
			    			    () -> API.meta().moduleInfo(ModuleInfoRequest.builder()
			    			    		.requestedModuleInfos(lockedModules)
			    			    		.build()),
			    			    result2 -> {
			    			    	if (result2 != null) {
			    			    		MenuModel.lockedModules = result2;
			    			    		MenuModel.moduleChanged = true;
			    			    		queue(LoadProfileMessage.builder().build());
			    			    		queue(LoadResourcesMessage.builder().build());
			    					} else {
			    						Logger.log("Menu", "ModuleData couldn't be loaded!");
			    					}
			    			    }
			    		);
					} else {
						Logger.log("Menu", "ModuleData couldn't be loaded!");
					}
			    }
		);
	}
	
	private static void reload() {
		menuRenderer.reload();
		for (BaseModuleRenderer renderer : renderers.values()) {
			renderer.reload();
		}
	}
	
	public static void requireReload(MenuModule module) {
		reloadRequired.add(module);
	}

	public static void dispose() {
		if (PlayTab.lobbyStatus != null && PlayTab.lobbyStatus.getCode() == LobbyStatusCode.MATCHING) {
			PlayTab.stopMatching();
		}
		close();
		menuRenderer.dispose();
		for (BaseModuleRenderer renderer : renderers.values()) {
			if (renderer != null) {
				renderer.dispose();
			}
		}
	}

}
