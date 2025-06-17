package de.instinct.eqfleet.game;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.engine.model.GameState;
import de.instinct.engine.net.message.types.PlayerAssigned;
import de.instinct.eqfleet.game.backend.driver.Driver;
import de.instinct.eqfleet.game.backend.driver.local.custom.CustomDriver;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.TutorialDriver;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.TutorialMode;
import de.instinct.eqfleet.game.backend.driver.online.OnlineDriver;
import de.instinct.eqfleet.game.frontend.GameRenderer;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.net.MessageQueue;

public class Game {

    private static GameRenderer renderer;
    
    private static Driver currentDriver;

    public static void init() {
    	GameModel.outputMessageQueue = new MessageQueue<>();
    	GameModel.inputMessageQueue = new MessageQueue<>();
    	GameModel.receivedGameState = new ConcurrentLinkedQueue<>();
    	renderer = new GameRenderer();
    }
    
    public static void start() {
    	currentDriver = new OnlineDriver();
    	renderer.init();
    	GameModel.active = true;
    	currentDriver.start();
    	GameModel.inputEnabled = true;
    }
    
    public static void startTutorial(TutorialMode mode) {
    	currentDriver = new TutorialDriver();
		renderer.init();
		GameModel.active = true;
		((TutorialDriver)currentDriver).setMode(mode);
		currentDriver.start();
	}
    
    public static void startCustom() {
    	currentDriver = new CustomDriver();
    	renderer.init();
    	GameModel.active = true;
    	currentDriver.start();
		GameModel.inputEnabled = true;
    }

    public static void stop() {
    	GameModel.inputEnabled = false;
    	currentDriver.stop();
	}

    public static void render() {
        if (GameModel.active) {
            if (!GameModel.receivedGameState.isEmpty()) {
            	GameModel.activeGameState = GameModel.receivedGameState.poll();
                GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
            }
            
            if (GameModel.activeGameState != null) {
            	currentDriver.update();
                renderer.render(GameModel.activeGameState);
            }
        }
    }
	
	public static void update(GameState newGameState) {
		GameModel.receivedGameState.add(newGameState);
	}

	public static void dispose() {
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

	public static void setVisible(boolean visible) {
		renderer.visible = visible;
	}

	public static void setUIElementVisible(String tag, boolean visible) {
		renderer.setUIElementVisible(tag, visible);
	}

}
