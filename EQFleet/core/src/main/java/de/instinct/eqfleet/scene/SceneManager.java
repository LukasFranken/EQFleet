package de.instinct.eqfleet.scene;

import java.util.HashMap;
import java.util.Map;

import de.instinct.eqfleet.intro.Intro;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class SceneManager {
	
	private static final String TAG = "SCENE_MANAGER";
	
	private static Map<SceneType, Scene> scenes;
	
	private static SceneType currentScene;
	
	public static void init() {
		scenes = new HashMap<>();
		scenes.put(SceneType.INTRO, new Intro());
		scenes.get(SceneType.INTRO).init();
		scenes.put(SceneType.MENU, new Menu());
		scenes.get(SceneType.MENU).init();
	}
	
	public static void update() {
		scenes.get(currentScene).update();
	}
	
	public static void render() {
		scenes.get(currentScene).render();
	}
	
	public static void changeTo(SceneType sceneType) {
		if (currentScene != null) scenes.get(currentScene).close();
		currentScene = sceneType;
		scenes.get(currentScene).open();
		Logger.log(TAG, "Changed to scene: " + sceneType, ConsoleColor.YELLOW);
	}
	
	public static void dispose() {
		for (Scene scene : scenes.values()) {
			scene.dispose();
		}
	}

}
