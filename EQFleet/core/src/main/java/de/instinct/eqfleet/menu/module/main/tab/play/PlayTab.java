package de.instinct.eqfleet.menu.module.main.tab.play;

public class PlayTab {
	
	private static PlayTabRenderer renderer;
	private static PlayTabLogic logic;

	public static void init() {
		renderer = new PlayTabRenderer();
		renderer.init();
		logic = new PlayTabLogic();
		logic.init();
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
