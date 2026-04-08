package de.instinct.eqfleet.scene;

import java.util.HashMap;
import java.util.Map;

import de.instinct.eqfleet.cover.Cover;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.intro.Intro;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.postgame.PostGame;
import de.instinct.eqfleet.mining.Mining;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class SceneManager {
	
	private static final String TAG = "SCENE_MANAGER";
	
	private static Map<SceneType, Scene> scenes;
	
	private static SceneType currentScene;
	
	public static void init() {
		scenes = new HashMap<>();
		scenes.put(SceneType.INTRO, new Intro());
		scenes.put(SceneType.MENU, new Menu());
		scenes.put(SceneType.GAME, new Game());
		scenes.put(SceneType.POSTGAME, new PostGame());
		scenes.put(SceneType.COVER, new Cover());
		scenes.put(SceneType.MINING, new Mining());
		
		for (SceneType sceneType : scenes.keySet()) {
			scenes.get(sceneType).init();
		}
	}
	
	public static void update() {
		if (currentScene != null) scenes.get(currentScene).update();
	}
	
	public static void render() {
		if (currentScene != null) scenes.get(currentScene).render();
	}
	
	public static void changeTo(SceneType sceneType) {
		if (currentScene != null) scenes.get(currentScene).close();
		currentScene = sceneType;
		scenes.get(currentScene).open();
		Logger.log(TAG, "Changed to scene: " + sceneType, ConsoleColor.YELLOW);
	}
	
	public static SceneType getCurrentScene() {
		return currentScene;
	}
	
	public static void dispose() {
		for (Scene scene : scenes.values()) {
			scene.dispose();
		}
	}

}
