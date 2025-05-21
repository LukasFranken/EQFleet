package de.instinct.eqfleet.menu.module.main.tab.loadout;

public class LoadoutTab {
	
	private static LoadoutTabRenderer renderer;

	public static void init() {
		renderer = new LoadoutTabRenderer();
	}

	public static void update() {
		
	}
	
	public static void render() {
		renderer.render();
	}
	
	public static void dispose() {
		renderer.dispose();
	}

}
