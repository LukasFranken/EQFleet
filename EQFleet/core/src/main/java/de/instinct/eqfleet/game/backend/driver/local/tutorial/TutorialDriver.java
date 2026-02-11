package de.instinct.eqfleet.game.backend.driver.local.tutorial;

import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine_api.core.model.GameStateInitialization;
import de.instinct.engine_api.core.service.GameStateInitializer;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.cover.CoverManager;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.driver.local.LocalDriver;
import de.instinct.eqfleet.language.LanguageManager;

public class TutorialDriver extends LocalDriver {

	private TutorialLoader tutorialLoader;
	private boolean finished;
    private TutorialMode mode;
    private GameStateInitializer gameStateInitializer;
    
    public TutorialDriver() {
    	super();
    	tutorialLoader = new TutorialLoader();
    	gameStateInitializer = new GameStateInitializer();
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
		GameModel.activeGameState = gameStateInitializer.initialize(initialGameState);
		GameModel.activeGameState.pauseData.resumeCountdownMS = 0;
		GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
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

	@Override
	protected void postEngineUpdate() {
		if (TutorialModel.skipped) {
			GameModel.activeGameState.winner = 1;
		}
		if (GameModel.activeGameState != null && GameModel.activeGameState.started && !finished) {
        	if (GameModel.activeGameState.winner != 0) {
    			GameModel.guidedEvents = null;
    			Game.stop();
    		}
    	}
	}

	@Override
	public long finish() {
		AudioManager.playVoice("tutorial/" + LanguageManager.getCurrentLanguage().getCode(), "voiceline_34");
		GameModel.guidedEvents = null;
		mode = null;
		finished = true;
		CoverManager.start();
		return CoverManager.getDuration() + 500;
	}

	@Override
	public void cleanup() {
		
	}
	
}
