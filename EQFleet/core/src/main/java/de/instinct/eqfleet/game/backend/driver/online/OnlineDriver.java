package de.instinct.eqfleet.game.backend.driver.online;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.net.message.types.GameFinishUpdate;
import de.instinct.engine.net.message.types.GameOrderUpdate;
import de.instinct.engine.net.message.types.GameStartUpdate;
import de.instinct.engine.net.message.types.JoinMessage;
import de.instinct.engine.net.message.types.PlayerAssigned;
import de.instinct.engine.stats.StatCollector;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.driver.Driver;

public class OnlineDriver extends Driver {
	
	private final int SMOOTHNESS_DELAY_MS = 200;
	private GameClient gameClient;
	
	public OnlineDriver() {
		super();
		gameClient = new GameClient();
	}

	@Override
	public void setup() {
		gameClient.start();
		JoinMessage joinMessage = new JoinMessage();
		joinMessage.playerUUID = API.authKey;
		GameModel.outputMessageQueue.add(joinMessage);
	}
	
	@Override
	protected void preEngineUpdate() {
		while (GameModel.inputMessageQueue.peek() != null) {
			Object object = GameModel.inputMessageQueue.next();
			if (object instanceof PlayerAssigned) {
				Game.assignPlayer((PlayerAssigned) object);
			}
			if (object instanceof GameState) {
				GameModel.receivedGameState.add((GameState) object);
			}
			if (object instanceof GameOrderUpdate) {
				GameModel.receivedOrders.add((GameOrderUpdate) object);
			}
			if (object instanceof GameStartUpdate) {
				GameModel.activeGameState.started = true;
			}
			if (object instanceof GameFinishUpdate) {
				GameModel.activeGameState.resultData = ((GameFinishUpdate) object).resultData;
			}
		}
	}

	@Override
	protected void postEngineUpdate() {
		if (GameModel.outputMessageQueue.peek() != null && gameClient.client.isConnected()) {
			gameClient.send(GameModel.outputMessageQueue.next());
		}
		if (!GameModel.receivedGameState.isEmpty()) {
        	GameModel.activeGameState = GameModel.receivedGameState.poll();
        	GameModel.activeGameState.pauseData.resumeCountdownMS += SMOOTHNESS_DELAY_MS;
        	if (!StatCollector.isInitialized(GameModel.activeGameState.gameUUID)) {
        		StatCollector.initialize(GameModel.activeGameState.gameUUID, GameModel.activeGameState.staticData.playerData.players);
        	}
            GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
            if (GameModel.activeGameState.resultData.winner != 0) {
    			Game.stop();
    		}
        }
		if (!GameModel.receivedOrders.isEmpty()) {
			GameModel.activeGameState.orderData.unprocessedOrders.addAll(GameModel.receivedOrders.poll().newOrders);
		}
	}

	@Override
	public long finish() {
		gameClient.stop();
		return 2000;
	}

	@Override
	public void cleanup() {
		gameClient.dispose();
	}

}
