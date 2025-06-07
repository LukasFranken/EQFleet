package de.instinct.eqfleet.game.backend.driver.local.tutorial;

import de.instinct.engine.initialization.GameStateInitialization;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.driver.local.LocalDriver;

public class TutorialDriver extends LocalDriver {

	private TutorialLoader tutorialLoader;
	
    private TutorialMode mode;
    
    public TutorialDriver() {
    	super();
    	tutorialLoader = new TutorialLoader();
    }
    
    public void setMode(TutorialMode mode) {
		this.mode = mode;
	}

    @Override
	public void setup() {
    	if (mode == null) {
			throw new IllegalStateException("Tutorial mode must be set before starting the tutorial driver.");
		}
		GameModel.guidedEvents = tutorialLoader.load(mode);
		GameModel.playerId = 1;
		GameStateInitialization initialGameState = tutorialLoader.generateInitialGameState();
		GameModel.activeGameState = engine.initializeGameState(initialGameState);
		GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
		new Runnable() {

			@Override
			public void run() {
				if (!GameModel.paused) {
					NetworkMessage newMessage = GameModel.outputMessageQueue.next();
					if (newMessage != null) {
						if (newMessage instanceof FleetMovementMessage) {
							engine.queue(GameModel.activeGameState, getOrder((FleetMovementMessage)newMessage));
						}
						if (newMessage instanceof LoadedMessage) {
							GameModel.activeGameState.started = true;
						}
					}
				}
			}
			
		};
	}
	
	private GameOrder getOrder(FleetMovementMessage message) {
		ShipMovementOrder order = new ShipMovementOrder();
		order.playerId = message.userUUID.contentEquals("2") ? 2 : 1;
		order.fromPlanetId = message.fromPlanetId;
		order.toPlanetId = message.toPlanetId;
		return order;
	}
	
	@Override
	protected void preEngineUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postEngineUpdate() {
		if (GameModel.activeGameState != null && GameModel.activeGameState.started) {
        	EngineUtility.checkVictory(GameModel.activeGameState);
        	if (GameModel.guidedEvents.isEmpty()) {
        		if (GameModel.activeGameState.winner != 0) {
        			GameModel.guidedEvents = null;
        			Game.stop();
        		}
        	}
    	}
	}

	@Override
	public void finish() {
		GameModel.activeGameState.winner = 1;
		GameModel.guidedEvents = null;
		mode = null;
	}

	@Override
	public void cleanup() {
		
	}
	
}
