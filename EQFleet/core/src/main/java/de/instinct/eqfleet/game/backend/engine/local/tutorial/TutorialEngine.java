package de.instinct.eqfleet.game.backend.engine.local.tutorial;

import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.backend.engine.local.LocalEngine;
import de.instinct.eqfleetshared.gamelogic.EngineUtility;
import de.instinct.eqfleetshared.gamelogic.EventEngine;
import de.instinct.eqfleetshared.gamelogic.order.model.GameOrder;
import de.instinct.eqfleetshared.gamelogic.order.model.subtypes.FleetMovementOrder;
import de.instinct.eqfleetshared.net.message.NetworkMessage;
import de.instinct.eqfleetshared.net.message.types.FleetMovementMessage;

public class TutorialEngine extends LocalEngine {
	
	private TutorialLoader tutorialLoader;
    private EventEngine engine;
    
    public TutorialEngine() {
    	tutorialLoader = new TutorialLoader();
    	engine = new EventEngine();
    }

	public Runnable start(TutorialMode mode) {
		Game.guidedEvents = tutorialLoader.load(mode);
		Game.getRendererConfig().setVisible(false);
		Game.factionId = 1;
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
		order.factionId = 
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
