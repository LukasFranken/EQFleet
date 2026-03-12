package de.instinct.eqfleet.game;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.engine.net.message.types.PlayerAssigned;
import de.instinct.eqfleet.game.backend.driver.Driver;
import de.instinct.eqfleet.game.backend.driver.local.custom.CustomDriver;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.TutorialDriver;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.TutorialMode;
import de.instinct.eqfleet.game.backend.driver.online.OnlineDriver;
import de.instinct.eqfleet.game.frontend.GameRenderer;
import de.instinct.eqfleet.scene.Scene;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.net.MessageQueue;

public class Game extends Scene {

    private static GameRenderer renderer;
    private static Driver currentDriver;
    private static TutorialMode tutorialMode;
    
    @Override
    public void init() {
    	GameModel.outputMessageQueue = new MessageQueue<>();
    	GameModel.inputMessageQueue = new MessageQueue<>();
    	GameModel.receivedGameState = new ConcurrentLinkedQueue<>();
    	GameModel.receivedOrders = new ConcurrentLinkedQueue<>();
    	renderer = new GameRenderer();
    }
    
    public static void start() {
    	currentDriver = new OnlineDriver();
    	renderer.init();
    	GameModel.active = true;
    	currentDriver.start();
    	GameModel.inputEnabled = true;
    	GameModel.visible = true;
    }
    
    public static void startTutorial(TutorialMode mode) {
    	tutorialMode = mode;
    	currentDriver = new TutorialDriver();
    	renderer.init();
		GameModel.active = true;
		((TutorialDriver)currentDriver).setMode(mode);
		currentDriver.start();
		GameModel.visible = false;
	}
    
    public static void startCustom() {
    	currentDriver = new CustomDriver();
    	renderer.init();
    	GameModel.active = true;
    	currentDriver.start();
		GameModel.inputEnabled = true;
		GameModel.visible = true;
    }

    public static void stop() {
    	currentDriver.stop();
    	GameModel.inputEnabled = false;
	}
    
    @Override
	public void open() {
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public void update() {
		if (GameModel.active) {
			currentDriver.update();
		}
	}

    @Override
    public void render() {
        if (GameModel.active) {
        	Profiler.startFrame("GAME");
            if (GameModel.activeGameState != null) {
                renderer.render(GameModel.activeGameState);
                Profiler.checkpoint("GAME", "render");
            }
            Profiler.endFrame("GAME");
        }
    }

    public static void end() {
    	GameModel.visible = false;
		GameModel.active = false;
		if (renderer != null) {
			renderer.dispose();
		}
		if (currentDriver != null) {
			currentDriver.dispose();
		}
		if (GameModel.lastGameUUID.contentEquals("tutorial")) {
			if (tutorialMode == TutorialMode.FULL) {
				SceneManager.changeTo(SceneType.COVER);
			} else {
				SceneManager.changeTo(SceneType.MENU);
			}
			tutorialMode = null;
		} else {
			if (GameModel.lastGameUUID == null || GameModel.lastGameUUID.contentEquals("custom")) {
				SceneManager.changeTo(SceneType.MENU);
			} else {
				SceneManager.changeTo(SceneType.POSTGAME);
			}
		}
    }
    
    @Override
	public void dispose() {
		GameModel.visible = false;
		GameModel.active = false;
		if (renderer != null) {
			renderer.dispose();
		}
		if (currentDriver != null) {
			currentDriver.dispose();
		}
	}

	public static void assignPlayer(PlayerAssigned playerAssigned) {
		GameModel.playerId = playerAssigned.playerId;
        Logger.log("GAME", "Assigned player ID: " + GameModel.playerId, ConsoleColor.GRAY);
	}

	public static void pause() {
		GameModel.paused = true;
	}
	
	public static void unpause() {
		GameModel.paused = false;
		GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
	}

	public static void setUIElementVisible(String tag, boolean visible) {
		renderer.setUIElementVisible(tag, visible);
	}

}
