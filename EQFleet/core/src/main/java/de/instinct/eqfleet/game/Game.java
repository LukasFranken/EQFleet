package de.instinct.eqfleet.game;

import java.util.Queue;
import java.util.UUID;

import de.instinct.engine.model.GameState;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.PlayerAssigned;
import de.instinct.eqfleet.game.backend.GameLogic;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.TutorialMode;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.frontend.GameRenderer;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.net.MessageQueue;

public class Game {

    private static GameRenderer renderer;
    private static GameLogic gameLogic;

    public static String playerUUID = UUID.randomUUID().toString();
    public static int playerId;
    public static GameState activeGameState;
    public static long lastUpdateTimestampMS;
    
    public static MessageQueue<NetworkMessage> outputMessageQueue;
    public static Queue<GuideEvent> guidedEvents;
    
    public static boolean paused;
    public static boolean inputEnabled;
    public static boolean active;

    public static void init() {
    	outputMessageQueue = new MessageQueue<>();
    	renderer = new GameRenderer();
    	gameLogic = new GameLogic();
    }
    
    public static void start() {
    	renderer.init();
    	active = true;
    	gameLogic.start();
    	inputEnabled = true;
    }

    public static void stop() {
    	inputEnabled = false;
    	gameLogic.stop();
	}

	public static void render() {
    	if (active) {
        	renderer.render();
    	}
    }
	
	public static void update(GameState newGameState) {
		Game.activeGameState = newGameState;
		Game.lastUpdateTimestampMS = System.currentTimeMillis();
    	if (Game.activeGameState.winner != 0) {
    		Game.stop();
    	}
	}

	public static void dispose() {
		gameLogic.dispose();
	}

	public static void assignPlayer(PlayerAssigned playerAssigned) {
		playerId = playerAssigned.playerId;
        Logger.log("Client", "Assigned player ID: " + playerId);
	}

	public static void startTutorial(TutorialMode mode) {
		renderer.init();
		active = true;
		gameLogic.startTutorial(mode);
	}

	public static void pause() {
		paused = true;
	}
	
	public static void unpause() {
		paused = false;
		Game.lastUpdateTimestampMS = System.currentTimeMillis();
	}

	public static void setVisible(boolean visible) {
		renderer.visible = visible;
	}

	public static void setUIElementVisible(String tag, boolean visible) {
		renderer.setUIElementVisible(tag, visible);
	}

}
