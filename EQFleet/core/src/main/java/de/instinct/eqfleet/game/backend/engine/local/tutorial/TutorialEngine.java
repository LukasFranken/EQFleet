package de.instinct.eqfleet.game.backend.engine.local.tutorial;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.EventEngine;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.FleetMovementOrder;
import de.instinct.eqfleet.game.Game;

public class TutorialEngine {
	
	private TutorialLoader tutorialLoader;
    private EventEngine engine;
    
    public TutorialEngine() {
    	tutorialLoader = new TutorialLoader();
    	engine = new EventEngine();
    }

	public Runnable start(TutorialMode mode) {
		Game.guidedEvents = tutorialLoader.load(mode);
		Game.setVisible(false);
		Game.playerId = 1;
		Game.activeGameState = tutorialLoader.generateGameState();
		Game.lastUpdateTimestampMS = System.currentTimeMillis();
		return new Runnable() {

			@Override
			public void run() {
				if (!Game.paused) {
					NetworkMessage newMessage = Game.outputMessageQueue.next();
					if (newMessage != null) {
						if (newMessage instanceof FleetMovementMessage) {
							engine.queue(Game.activeGameState, getOrder((FleetMovementMessage)newMessage));
						}
					}
					update();
				}
			}
			
		};
	}
	
	private GameOrder getOrder(FleetMovementMessage message) {
		FleetMovementOrder order = new FleetMovementOrder();
		order.playerId = message.userUUID.contentEquals("2") ? 2 : 1;
		order.fromPlanetId = message.fromPlanetId;
		order.toPlanetId = message.toPlanetId;
		return order;
	}

	private void update() {
		if (Game.activeGameState != null) {
    		long currentTime = System.currentTimeMillis();
        	engine.update(Game.activeGameState, currentTime - Game.lastUpdateTimestampMS);
        	EngineUtility.checkVictory(Game.activeGameState);
        	Game.lastUpdateTimestampMS = currentTime;
        	if (Game.guidedEvents.isEmpty()) {
        		if (Game.activeGameState.winner != 0) {
        			Game.guidedEvents = null;
        			Game.stop();
        		}
        	}
    	}
	}

	public void stop() {
		Game.activeGameState.winner = 1;
		Game.guidedEvents = null;
	}

}
