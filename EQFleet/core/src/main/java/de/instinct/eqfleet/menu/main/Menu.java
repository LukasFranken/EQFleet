package de.instinct.eqfleet.menu.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
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
	
	private static boolean active;
	
	private static Queue<ModuleMessage> moduleMessageQueue;
	private static Queue<MenuModule> reloadRequired;
	
	public static void init() {
		menuRenderer = new MenuRenderer();
		reloadRequired = new ConcurrentLinkedQueue<>();
		
		modules = new HashMap<>();
		renderers = new HashMap<>();
		
		active = false;
		
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
		
		moduleMessageQueue = new ConcurrentLinkedQueue<>();
	}
	
	public static void open() {
		loadModules();
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			update();
		}, 0, UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
		active = true;
		PlayTab.init();
		PlayTab.loadData();
		//AudioManager.play("neon_horizon_ambient", true);
	}
	
	public static void close() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
		active = false;
		//AudioManager.stop();
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
			if (MenuModel.modules != null && MenuModel.modules.getEnabledModules().contains(module.getMenuModule())) {
				module.update();
			}
		}
	}
	
	public static void render() {
		while (reloadRequired.peek() != null) {
			renderers.get(reloadRequired.poll()).reload();
		}
		if (active) {
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
			    		MenuModel.modules = result;
			    		reload();
			    		queue(LoadProfileMessage.builder().build());
			    		queue(LoadResourcesMessage.builder().build());
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
		close();
		menuRenderer.dispose();
		for (BaseModuleRenderer renderer : renderers.values()) {
			renderer.dispose();
		}
	}

}
