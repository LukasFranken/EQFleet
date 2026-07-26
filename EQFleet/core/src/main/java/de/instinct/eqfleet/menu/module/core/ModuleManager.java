package de.instinct.eqfleet.menu.module.core;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.PreferenceManager;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;

public class ModuleManager {
	
	private List<InitialModuleOpenMessage> moduleOpenMessages;
	
	public ModuleManager() {
		String preferenceLoadedModules = PreferenceManager.load("initialmodule");
		moduleOpenMessages = new ArrayList<>();
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.MINING)
				.message("Mine asteroids,\ngather resources,\nearn credits and\nupgrade your ship!")
				.opened(preferenceLoadedModules.contains(MenuModule.CONQUEST.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.CONQUEST)
				.message("Prepare for battle\nand conquer the galaxy!\n\n ...and more.")
				.opened(preferenceLoadedModules.contains(MenuModule.CONQUEST.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.PROFILE)
				.message("Before you can access your profile,\nyou need to choose a unique name")
				.opened(preferenceLoadedModules.contains(MenuModule.PROFILE.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.SETTINGS)
				.message("Settings are stored on the server\nand will transfer across devices")
				.opened(preferenceLoadedModules.contains(MenuModule.SETTINGS.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.SOCIAL)
				.message("Connect with other commanders,\ngroup up, form alliances.\n\nThe universe is too vast to\nconquer it alone!")
				.opened(preferenceLoadedModules.contains(MenuModule.SOCIAL.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.MARKET)
				.message("Trade resources with\nother commanders")
				.opened(preferenceLoadedModules.contains(MenuModule.MARKET.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.STORAGE)
				.message("Store your resources safely\nin your station storage")
				.opened(preferenceLoadedModules.contains(MenuModule.STORAGE.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.FORGE)
				.message("Create sophisticated goods\nfrom your resources")
				.opened(preferenceLoadedModules.contains(MenuModule.FORGE.toString()))
				.build());
	}
	
	public void openModule(MenuModule module) {
		MenuModel.activeModule = module;
		MenuModel.modules.get(module).load();
		MenuModel.renderers.get(module).init();
		for (InitialModuleOpenMessage message : moduleOpenMessages) {
			if (message.getModule() == module) {
				if (!message.isOpened()) {
					message.setOpened(true);
					String loadedString = PreferenceManager.load("initialmodule");
					PreferenceManager.save("initialmodule", (loadedString.isEmpty() ? "" : (loadedString + ",")) + message.getModule().toString());
					PopupRenderer.createMessageDialog(module.toString(), message.getMessage());
				}
				break;
			}
		}
	}

}
