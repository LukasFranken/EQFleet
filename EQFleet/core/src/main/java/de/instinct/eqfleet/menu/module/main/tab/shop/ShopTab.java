package de.instinct.eqfleet.menu.module.main.tab.shop;

public class ShopTab {

private static ShopTabRenderer renderer;
	
	public static void init() {
		renderer = new ShopTabRenderer();
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
