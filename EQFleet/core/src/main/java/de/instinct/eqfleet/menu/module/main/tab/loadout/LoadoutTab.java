package de.instinct.eqfleet.menu.module.main.tab.loadout;

public class LoadoutTab {
	
	private static LoadoutTabLogic logic;
	private static LoadoutTabRenderer renderer;

	public static void init() {
		logic = new LoadoutTabLogic();
		logic.init();
		renderer = new LoadoutTabRenderer();
		renderer.init();
	}

	public static void update() {
		logic.update();
	}
	
	public static void render() {
		renderer.render();
	}
	
	public static void dispose() {
		renderer.dispose();
	}

}
