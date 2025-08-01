package de.instinct.eqfleet.menu.module.core;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;

public class ModuleManager {
	
	private static List<InitialModuleOpenMessage> moduleOpenMessages;
	
	public static void init() {
		String preferenceLoadedModules = PreferenceUtil.load("initialmodule");
		moduleOpenMessages = new ArrayList<>();
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.STARMAP)
				.message("Prepare for battle\nand conquer the galaxy!\n\n Remember to regularly check the\nmenu for unlocked modules!")
				.opened(preferenceLoadedModules.contains(MenuModule.STARMAP.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.SHIPYARD)
				.message("View your ships and select\nthem for use in battle")
				.opened(preferenceLoadedModules.contains(MenuModule.SHIPYARD.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.WORKSHOP)
				.message("Upgrade ships and turrets\nand build new ones from\nblueprints you own")
				.opened(preferenceLoadedModules.contains(MenuModule.WORKSHOP.toString()))
				.build());
		moduleOpenMessages.add(InitialModuleOpenMessage.builder()
				.module(MenuModule.CONSTRUCTION)
				.message("View your turrets and select\nthem for use in battle")
				.opened(preferenceLoadedModules.contains(MenuModule.CONSTRUCTION.toString()))
				.build());
	}
	
	public static void openModule(MenuModule module) {
		for (InitialModuleOpenMessage message : moduleOpenMessages) {
			if (message.getModule() == module) {
				if (!message.isOpened()) {
					message.setOpened(true);
					String loadedString = PreferenceUtil.load("initialmodule");
					PreferenceUtil.save("initialmodule", (loadedString.isEmpty() ? "" : (loadedString + ",")) + message.getModule().toString());
					PopupRenderer.createMessageDialog(module.toString(), message.getMessage());
				}
				break;
			}
		}
	}

}
