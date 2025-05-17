package de.instinct.eqfleet.menu.module.main;

import de.instinct.eqfleet.menu.MenuTab;
import de.instinct.eqfleet.menu.module.main.tab.inventory.InventoryTab;
import de.instinct.eqfleet.menu.module.main.tab.loadout.LoadoutTab;
import de.instinct.eqfleet.menu.module.main.tab.play.PlayTab;
import de.instinct.eqfleet.menu.module.main.tab.profile.ProfileTab;
import de.instinct.eqfleet.menu.module.main.tab.settings.SettingsTab;
import de.instinct.eqfleet.menu.module.main.tab.shop.ShopTab;

public class MainMenu {
	
	public static MenuTab currentTab;
	
	public static MainMenuRenderer renderer;
	public static MainMenuLogic logic;
	
	public static void init() {
		renderer = new MainMenuRenderer();
		renderer.init();
		logic = new MainMenuLogic();
		logic.init();
		
		PlayTab.init();
		LoadoutTab.init();
		ProfileTab.init();
		SettingsTab.init();
		InventoryTab.init();
		ShopTab.init();
	}
	
	public static void update() {
		logic.update();
		PlayTab.update();
		LoadoutTab.update();
		ProfileTab.update();
		SettingsTab.update();
		InventoryTab.update();
		ShopTab.update();
	}
	
	public static void render() {
		renderer.render();
	}
	
	public static void dispose() {
		renderer.dispose();
	}

	public static void changeTab(MenuTab tab) {
		switch (tab) {
		case PROFILE:
			ProfileTab.loadData();
			break;
		case PLAY:
			PlayTab.loadData();
			break;
		case INVENTORY:
			InventoryTab.loadData();
			break;
		case SHOP:
			ShopTab.loadData();
			break;
			
		default:
			break;
		}
		currentTab = tab;
	}

}
