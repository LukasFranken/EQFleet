package de.instinct.eqfleet.game;

import java.util.Queue;
import java.util.UUID;

import de.instinct.engine.model.GameState;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.frontend.InteractionMode;
import de.instinct.eqlibgdxutils.net.MessageQueue;

public class GameModel {
	
	public static volatile MessageQueue<Object> inputMessageQueue;
	public static volatile MessageQueue<NetworkMessage> outputMessageQueue;
    public static volatile Queue<GuideEvent> guidedEvents;
    
    public static volatile boolean paused;
    public static volatile boolean inputEnabled;
    public static volatile boolean active;
    public static volatile boolean visible;
    public static volatile InteractionMode mode;
	
	public static volatile String playerUUID = UUID.randomUUID().toString();
    public static volatile int playerId;
    public static volatile Queue<GameState> receivedGameState;
    public static volatile GameState activeGameState;
    public static volatile long lastUpdateTimestampMS;

}
