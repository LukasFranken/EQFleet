package de.instinct.eqfleet.game;

import java.util.Queue;
import java.util.UUID;

import de.instinct.engine.core.net.NetworkMessage;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.net.messages.GameFinishUpdate;
import de.instinct.engine.fleet.net.messages.GameOrderUpdate;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.frontend.InteractionMode;
import de.instinct.eqfleet.game.frontend.ui.model.UIBounds;
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
    public static volatile Queue<FleetGameState> receivedGameState;
    public static volatile Queue<GameOrderUpdate> receivedOrders;
    public static volatile GameFinishUpdate receivedResults;
    public static volatile FleetGameState activeGameState;
    public static volatile long lastUpdateTimestampMS;
    
    public static volatile String lastGameUUID;
    
    public static volatile UIBounds uiBounds;

}
