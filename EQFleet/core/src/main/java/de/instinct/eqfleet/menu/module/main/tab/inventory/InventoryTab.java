package de.instinct.eqfleet.menu.module.main.tab.inventory;

public class InventoryTab {
	
	private static InventoryTabRenderer renderer;
	
	public static void init() {
		renderer = new InventoryTabRenderer();
		renderer.init();
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
