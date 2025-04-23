package de.instinct.eqfleet.game;

import java.util.Queue;
import java.util.UUID;

import com.badlogic.gdx.Gdx;

import de.instinct.eqfleet.game.backend.GameLogic;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.TutorialMode;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.frontend.GameRenderer;
import de.instinct.eqfleet.game.frontend.GameRendererConfig;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.net.message.NetworkMessage;
import de.instinct.eqfleetshared.net.message.types.MatchmakingRequest;
import de.instinct.eqfleetshared.net.message.types.MatchmakingUpdateResponse;
import de.instinct.eqfleetshared.net.message.types.PlayerAssigned;
import de.instinct.eqlibgdxutils.net.MessageQueue;

public class Game {

    private static GameRenderer renderer;
    private static GameLogic gameLogic;

    public static String playerUUID = UUID.randomUUID().toString();
    public static int factionId;
    public static GameState activeGameState;
    public static long lastUpdateTimestampMS;
    public static MatchmakingUpdateResponse matchmakingStatus;
    public static MatchmakingRequest currentMatchmakingRequest;
    
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
    
    public static void start(MatchmakingRequest matchmakingRequest) {
    	renderer.init();
    	matchmakingRequest.playerUUID = playerUUID;
    	outputMessageQueue.add(matchmakingRequest);
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
		gameLogic.update(newGameState);
	}

	public static void dispose() {
		gameLogic.dispose();
	}

	public static void assignPlayer(PlayerAssigned playerAssigned) {
		matchmakingStatus = null;
        factionId = playerAssigned.factionId;
        Gdx.app.log("Client", "Assigned player ID: " + factionId);
	}

	public static void startTutorial(TutorialMode mode) {
		renderer.init();
		active = true;
		gameLogic.startTutorial(mode);
	}
	
	public static GameRendererConfig getRendererConfig() {
		return renderer.getConfig();
	}

	public static void pause() {
		paused = true;
	}
	
	public static void unpause() {
		paused = false;
		Game.lastUpdateTimestampMS = System.currentTimeMillis();
	}

}
