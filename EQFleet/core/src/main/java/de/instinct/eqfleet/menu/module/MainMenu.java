package de.instinct.eqfleet.menu.module;

import de.instinct.eqfleet.menu.MenuTab;
import de.instinct.eqfleet.menu.module.main.tab.inventory.InventoryTab;
import de.instinct.eqfleet.menu.module.main.tab.loadout.LoadoutTab;
import de.instinct.eqfleet.menu.module.main.tab.play.PlayTab;
import de.instinct.eqfleet.menu.module.main.tab.settings.SettingsTab;
import de.instinct.eqfleet.menu.module.main.tab.station.StationTab;

public class MainMenu {
	
	public static MenuTab currentTab;
	
	public static void init() {
		
		PlayTab.init();
		LoadoutTab.init();
		SettingsTab.init();
		InventoryTab.init();
		StationTab.init();
	}
	
	private static void update() {
		
	}
	
	public static void render() {
		
	}
	
	public static void dispose() {
		
	}

	public static void changeTab(MenuTab tab) {
		
	}

}
