package de.instinct.eqfleet.menu.module.main.tab.inventory;

import de.instinct.api.core.API;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.net.WebManager;

public class InventoryTab {
	
	private static InventoryTabRenderer renderer;
	
	public static void init() {
		renderer = new InventoryTabRenderer();
	}

	public static void update() {
		
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
