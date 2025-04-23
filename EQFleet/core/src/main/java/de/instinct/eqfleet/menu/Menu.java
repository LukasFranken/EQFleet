package de.instinct.eqfleet.menu;

import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.backend.audio.AudioManager;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.TutorialMode;
import de.instinct.eqfleet.menu.module.main.MainMenu;

public class Menu {
	
	private static boolean active;
	
	public static void init() {
		MainMenu.init();
	}

	public static void load() {
		activate();
	}

	public static void render() {
		if (active) {
			MainMenu.render();
		}
	}
	
	public static void activate() {
		active = true;
		AudioManager.play("neon_horizon_ambient", true);
	}
	
	public static void deactivate() {
		active = false;
		//AudioManager.stop();
	}
	
	public static void loadTutorial(TutorialMode mode) {
		Game.startTutorial(mode);
	}

	public static void dispose() {
		MainMenu.dispose();
	}

}
