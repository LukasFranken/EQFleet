package de.instinct.eqfleet.menu.module.main.tab.settings;

public class SettingsTab {
	
	private static SettingsTabRenderer renderer;

	public static void init() {
		renderer = new SettingsTabRenderer();
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
