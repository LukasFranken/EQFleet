package de.instinct.eqfleet.menu.module.main.tab.station;

import de.instinct.eqfleet.menu.module.main.tab.shop.ShopCategory;
import de.instinct.eqfleet.menu.module.main.tab.shop.ShopTab;
import de.instinct.eqfleet.menu.module.main.tab.shop.ShopTabRenderer;

public class StationTab {
	
	private static ShopTabRenderer renderer;
	
	private static ShopCategory category;
	
	public static void init() {
		renderer = new ShopTabRenderer();
		ShopTab.init();
	}

	public static void update() {
		ShopTab.update();
	}
	
	public static void loadData() {
		
	}
	
	public static void render() {
		renderer.render();
	}
	
	public static void dispose() {
		renderer.dispose();
	}

}
