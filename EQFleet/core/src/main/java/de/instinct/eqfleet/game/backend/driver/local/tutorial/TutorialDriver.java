package de.instinct.eqfleet.game.backend.driver.local.tutorial;

import de.instinct.engine.core.net.NetworkMessage;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.fleet.net.messages.FleetMovementMessage;
import de.instinct.engine.fleet.net.messages.LoadedMessage;
import de.instinct.engine.fleet.order.types.ShipMovementOrder;
import de.instinct.engine_api.core.service.EngineDataInterface;
import de.instinct.engine_api.fleet.FleetGameStateInitializer;
import de.instinct.engine_api.fleet.model.FleetGameStateInitialization;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.driver.local.LocalDriver;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.language.LanguageManager;

public class TutorialDriver extends LocalDriver {

	private TutorialLoader tutorialLoader;
	private boolean finished;
    private TutorialMode mode;
    private FleetGameStateInitializer gameStateInitializer;
    
    public TutorialDriver() {
    	super();
    	tutorialLoader = new TutorialLoader();
    	gameStateInitializer = new FleetGameStateInitializer();
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
		FleetGameStateInitialization initialGameState = tutorialLoader.generateInitialGameState();
		GameModel.activeGameState = gameStateInitializer.initializeFleet(initialGameState);
		GameModel.activeGameState.metaData.pauseData.resumeCountdownMS = 0;
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
				EngineDataInterface.queue(GameModel.activeGameState, getOrder((FleetMovementMessage)newMessage));
			}
			if (newMessage instanceof LoadedMessage) {
				GameModel.activeGameState.started = true;
			}
		}
	}

	@Override
	protected void postEngineUpdate() {
		if (TutorialModel.skipped) {
			GameModel.activeGameState.resultData.winner = 1;
			GuideEvent lastElement = null;
			while (GameModel.guidedEvents.size() > 0) {
				lastElement = GameModel.guidedEvents.poll();
			}
			GameModel.guidedEvents.add(lastElement);
			TutorialModel.skipped = false;
		}
		if (GameModel.activeGameState != null && GameModel.activeGameState.started && !finished) {
        	if (GameModel.activeGameState.resultData.winner != 0) {
    			Game.stop();
    		}
    	}
	}

	@Override
	public long finish() {
		mode = null;
		finished = true;
		return (long) (AudioManager.getVoiceDuration("tutorial/" + LanguageManager.getCurrentLanguage().getCode(), "voiceline_34") * 1000) + 100;
	}

	@Override
	public void cleanup() {
		GameModel.guidedEvents = null;
	}
	
}
