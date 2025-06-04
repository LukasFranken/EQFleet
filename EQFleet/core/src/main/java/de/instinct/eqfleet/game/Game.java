package de.instinct.eqfleet.game;

import de.instinct.engine.model.GameState;
import de.instinct.engine.net.message.types.PlayerAssigned;
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
    
    private static OnlineDriver onlineDriver;
    private static CustomDriver customDriver;
    private static TutorialDriver tutorialDriver;

    public static void init() {
    	GameModel.outputMessageQueue = new MessageQueue<>();
    	GameModel.inputMessageQueue = new MessageQueue<>();
    	renderer = new GameRenderer();
    	onlineDriver = new OnlineDriver();
    	customDriver = new CustomDriver();
    	tutorialDriver = new TutorialDriver();
    }
    
    public static void start() {
    	renderer.init();
    	GameModel.active = true;
    	onlineDriver.start();
    	GameModel.inputEnabled = true;
    }
    
    public static void startTutorial(TutorialMode mode) {
		renderer.init();
		GameModel.active = true;
		tutorialDriver.setMode(mode);
		tutorialDriver.start();
	}
    
    public static void startCustom() {
    	renderer.init();
    	GameModel.active = true;
		customDriver.start();
		GameModel.inputEnabled = true;
    }

    public static void stop() {
    	GameModel.inputEnabled = false;
    	onlineDriver.stop();
    	customDriver.stop();
    	tutorialDriver.stop();
	}

	public static void render() {
    	if (GameModel.active) {
        	renderer.render(GameModel.activeGameState);
    	}
    }
	
	public static void update(GameState newGameState) {
		GameModel.activeGameState = newGameState;
		GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
    	if (GameModel.activeGameState.winner != 0) {
    		Game.stop();
    	}
	}

	public static void dispose() {
		renderer.dispose();
		onlineDriver.dispose();
    	customDriver.dispose();
    	tutorialDriver.dispose();
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
