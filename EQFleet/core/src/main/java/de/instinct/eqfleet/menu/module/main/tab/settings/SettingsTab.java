package de.instinct.eqfleet.menu.module.main.tab.settings;

public class SettingsTab {
	
	private static SettingsTabLogic logic;
	private static SettingsTabRenderer renderer;

	public static void init() {
		logic = new SettingsTabLogic();
		logic.init();
		renderer = new SettingsTabRenderer();
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
