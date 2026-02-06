package de.instinct.eqfleet.game.backend.driver.online;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.net.message.types.JoinMessage;
import de.instinct.engine.net.message.types.PlayerAssigned;
import de.instinct.engine.stats.StatCollector;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.driver.Driver;

public class OnlineDriver extends Driver {
	
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
			} else if (object instanceof GameState) {
				Game.update((GameState) object);
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
        	if (!StatCollector.isInitialized(GameModel.activeGameState.gameUUID)) {
        		StatCollector.initialize(GameModel.activeGameState.gameUUID, GameModel.activeGameState.players);
        	}
            GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
            if (GameModel.activeGameState.winner != 0) {
    			Game.stop();
    		}
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
