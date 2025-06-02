package de.instinct.eqfleet.game.backend.driver.online;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.JoinMessage;
import de.instinct.engine.net.message.types.PlayerAssigned;
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
		NetworkMessage newMessage = GameModel.outputMessageQueue.next();
		if (newMessage != null) {
			gameClient.send(newMessage);
		}
	}

	@Override
	public void finish() {
		gameClient.stop();
	}

	@Override
	public void cleanup() {
		gameClient.dispose();
	}

}
