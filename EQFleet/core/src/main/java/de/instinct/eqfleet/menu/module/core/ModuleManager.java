package de.instinct.eqfleet.menu.module.core;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.PreferenceManager;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;

public class ModuleManager {
	
	private static List<InitialModuleOpenMessage> moduleOpenMessages;
	
	public static void init() {
		String preferenceLoadedModules = PreferenceManager.load("initialmodule");
		moduleOpenMessages = new ArrayList<>();
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.STARMAP)
				.message("Prepare for battle\nand conquer the galaxy!\n\n ...and more.")
				.opened(preferenceLoadedModules.contains(MenuModule.STARMAP.toString()))
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
				.module(MenuModule.SHIPYARD)
				.message("Build and equip your first ship")
				.opened(preferenceLoadedModules.contains(MenuModule.SHIPYARD.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.SHOP)
				.message("Time to put your\nhard earned credits to use")
				.opened(preferenceLoadedModules.contains(MenuModule.SHOP.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.PLANET)
				.message("View your buildings and select\nthem for use in battle")
				.opened(preferenceLoadedModules.contains(MenuModule.PLANET.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.CONSTRUCTION)
				.message("Build and upgrade your\ninfrastructure\n\nConstruction mode unlocked!")
				.opened(preferenceLoadedModules.contains(MenuModule.CONSTRUCTION.toString()))
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
	}
	
	public static void openModule(MenuModule module) {
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
