package de.instinct.eqfleet.menu.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.meta.dto.modules.ModuleData;
import de.instinct.api.meta.dto.modules.ModuleInfoRequest;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.main.message.MenuMessage;
import de.instinct.eqfleet.menu.main.message.types.OpenModuleMessage;
import de.instinct.eqfleet.menu.module.construction.Construction;
import de.instinct.eqfleet.menu.module.construction.ConstructionRenderer;
import de.instinct.eqfleet.menu.module.core.ModuleManager;
import de.instinct.eqfleet.menu.module.play.Play;
import de.instinct.eqfleet.menu.module.play.PlayModel;
import de.instinct.eqfleet.menu.module.play.PlayRenderer;
import de.instinct.eqfleet.menu.module.profile.Profile;
import de.instinct.eqfleet.menu.module.profile.ProfileRenderer;
import de.instinct.eqfleet.menu.module.settings.Settings;
import de.instinct.eqfleet.menu.module.settings.SettingsRenderer;
import de.instinct.eqfleet.menu.module.ship.Shipyard;
import de.instinct.eqfleet.menu.module.ship.ShipyardRenderer;
import de.instinct.eqfleet.menu.module.shop.Shop;
import de.instinct.eqfleet.menu.module.shop.ShopRenderer;
import de.instinct.eqfleet.menu.module.social.Social;
import de.instinct.eqfleet.menu.module.social.SocialRenderer;
import de.instinct.eqfleet.menu.module.starmap.Starmap;
import de.instinct.eqfleet.menu.module.starmap.StarmapRenderer;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.net.model.ConnectionStatus;
import de.instinct.eqfleet.scene.Scene;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;

public class Menu extends Scene {
	
	private ModuleManager moduleManager;
	private MenuRenderer menuRenderer;
	private GridRenderer gridRenderer;
	
	@Override
	public void init() {
		MenuModel.messageQueue = new ConcurrentLinkedQueue<>();
		
		moduleManager = new ModuleManager();
		menuRenderer = new MenuRenderer();
		menuRenderer.init();
		
		gridRenderer = new GridRenderer(GridConfiguration.builder()
				.step(10f)
				.build());
		
		MenuModel.modules = new HashMap<>();
		MenuModel.renderers = new LinkedHashMap<>();
		
		MenuModel.renderers.put(MenuModule.PROFILE, new ProfileRenderer());
		MenuModel.renderers.put(MenuModule.SETTINGS, new SettingsRenderer());
		MenuModel.renderers.put(MenuModule.STARMAP, new StarmapRenderer());
		MenuModel.renderers.put(MenuModule.SHIPYARD, new ShipyardRenderer());
		MenuModel.renderers.put(MenuModule.CONSTRUCTION, new ConstructionRenderer());
		MenuModel.renderers.put(MenuModule.SHOP, new ShopRenderer());
		MenuModel.renderers.put(MenuModule.PLAY, new PlayRenderer());
		MenuModel.renderers.put(MenuModule.SOCIAL, new SocialRenderer());
		
		MenuModel.modules.put(MenuModule.PLAY, new Play());
		MenuModel.modules.put(MenuModule.PROFILE, new Profile());
		MenuModel.modules.put(MenuModule.SETTINGS, new Settings());
		MenuModel.modules.put(MenuModule.SHIPYARD, new Shipyard());
		MenuModel.modules.put(MenuModule.CONSTRUCTION, new Construction());
		MenuModel.modules.put(MenuModule.SHOP, new Shop());
		MenuModel.modules.put(MenuModule.STARMAP, new Starmap());
		MenuModel.modules.put(MenuModule.SOCIAL, new Social());
		
		for (BaseModuleRenderer renderer : MenuModel.renderers.values()) {
			renderer.init();
		}
		
		for (BaseModule module : MenuModel.modules.values()) {
			module.init();
			module.load();
		}
		
		MenuModel.loaded = false;
	}
	
	@Override
	public void open() {
		menuRenderer.init();
		AudioManager.startRadio();
		load();
	}
	
	@Override
	public void close() {
		MenuModel.activeModule = null;
	}
	
	private void load() {
		MenuModel.loaded = false;
		loadModules();
		if (PlayModel.lobbyStatus != null && PlayModel.lobbyStatus.getType().getGameMode() == GameMode.CONQUEST) Play.leaveLobby();
	}
	
	@Override
	public void update() {
		if (!MenuModel.messageQueue.isEmpty()) {
			processMessage(MenuModel.messageQueue.poll());
		}
		for (BaseModule module : MenuModel.modules.values()) {
			if (MenuModel.unlockedModules != null) {
				module.update();
			}
		}
		if (MenuModel.activeModule != null) {
			MenuModel.renderers.get(MenuModel.activeModule).update();
		}
		menuRenderer.update();
	}
	
	private void processMessage(MenuMessage message) {
		if (message instanceof OpenModuleMessage) {
			moduleManager.openModule(((OpenModuleMessage) message).getModule());
		}
	}

	@Override
	public void render() {
		Profiler.startFrame("MENU");
		if (MenuModel.loaded) {
			if (GlobalStaticData.showDebugGrid) gridRenderer.drawGrid();
			Profiler.checkpoint("MENU", "grid render");
			if (MenuModel.activeModule != null) {
				MenuModel.renderers.get(MenuModel.activeModule).render();
				Profiler.checkpoint("MENU", "module render");
			}
			menuRenderer.render();
			Profiler.checkpoint("MENU", "menu render");
		}
		Profiler.endFrame("MENU");
	}
	
	private void loadModules() {
		if (WebManager.status == ConnectionStatus.ONLINE) {
			MenuModel.modules.get(MenuModule.PROFILE).load();
			WebManager.enqueue(
				    () -> API.meta().modules(API.authKey),
				    modulesResult -> {
				    	modulesResult.getEnabledModules().add(MenuModule.PLAY);
				    	processModulesResult(modulesResult);
				    }
			);
		} else {
			ModuleData offlineModulesResult = new ModuleData();
			offlineModulesResult.setEnabledModules(new ArrayList<>());
			offlineModulesResult.getEnabledModules().add(MenuModule.SETTINGS);
			offlineModulesResult.getEnabledModules().add(MenuModule.PLAY);
			processModulesResult(offlineModulesResult);
		}
	}
	
	private void processModulesResult(ModuleData modulesResult) {
		if (modulesResult != null) {
    		MenuModel.unlockedModules = modulesResult;
    		List<MenuModule> lockedModules = new ArrayList<>();
    		for (MenuModule module : MenuModule.values()) {
    			if (!MenuModel.unlockedModules.getEnabledModules().contains(module)) {
    				lockedModules.add(module);
    			}
    		}
    		//loadButtons();
    		loadLockedModuleInfo(lockedModules);
		} else {
			Logger.log("Menu", "ModuleData couldn't be loaded!", ConsoleColor.RED);
		}
	}

	/*private void loadButtons() {
		MenuModel.buttons = new ArrayList<>();
		if (MenuModel.unlockedModules != null && MenuModel.unlockedModules.getEnabledModules() != null) {
	        for (MenuModule module : MenuModel.renderers.keySet()) {
	            if (MenuModel.unlockedModules.getEnabledModules().contains(module)) {
	                MenuModel.buttons.add(module);
	            }
	        }
	    }
	}*/

	private void loadLockedModuleInfo(List<MenuModule> lockedModules) {
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
			    	MenuModel.loaded = true;
			    }
		);
	}

	@Override
	public void dispose() {
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
