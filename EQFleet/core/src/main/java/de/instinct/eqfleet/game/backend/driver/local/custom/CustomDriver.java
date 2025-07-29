package de.instinct.eqfleet.game.backend.driver.local.custom;

import java.util.List;

import de.instinct.api.core.API;
import de.instinct.api.meta.dto.LoadoutData;
import de.instinct.engine.ai.AiEngine;
import de.instinct.engine.initialization.GameStateInitialization;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.Player;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.net.message.types.GamePauseMessage;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.net.message.types.SurrenderMessage;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.GamePauseOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.order.types.SurrenderOrder;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.driver.local.LocalDriver;

public class CustomDriver extends LocalDriver {
	
	private CustomLoader customLoader;
	private AiEngine aiEngine;
	private boolean finished;
	
	public CustomDriver() {
		super();
		customLoader = new CustomLoader();
		aiEngine = new AiEngine();
	}

	@Override
	public void setup() {
		LoadoutData loadout = API.meta().loadout(API.authKey);
		GameModel.playerId = 1;
		GameStateInitialization initialGameState = customLoader.generateInitialGameState(loadout);
		GameModel.activeGameState = engine.initializeGameState(initialGameState);
		GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
		finished = false;
	}
	
	@Override
	protected void preEngineUpdate() {
		if (!GameModel.paused && GameModel.activeGameState != null) {
			NetworkMessage newMessage = GameModel.outputMessageQueue.next();
			if (newMessage != null) {
				if (newMessage instanceof FleetMovementMessage) {
					engine.queue(GameModel.activeGameState, getOrder((FleetMovementMessage)newMessage));
				}
				if (newMessage instanceof GamePauseMessage) {
					engine.queue(GameModel.activeGameState, getOrder((GamePauseMessage)newMessage));
				}
				if (newMessage instanceof SurrenderMessage) {
					engine.queue(GameModel.activeGameState, getOrder((SurrenderMessage)newMessage));
				}
				if (newMessage instanceof LoadedMessage) {
					GameModel.activeGameState.started = true;
				}
			}
		}
	}

	private GameOrder getOrder(FleetMovementMessage message) {
		ShipMovementOrder order = new ShipMovementOrder();
		order.playerId = message.userUUID.contentEquals(API.authKey) ? 1 : 2;
		order.fromPlanetId = message.fromPlanetId;
		order.toPlanetId = message.toPlanetId;
		order.playerShipId = message.shipId;
		return order;
	}
	
	private GameOrder getOrder(GamePauseMessage message) {
		GamePauseOrder order = new GamePauseOrder();
		order.playerId = message.userUUID.contentEquals(API.authKey) ? 1 : 2;
		order.pause = message.pause;
		order.reason = message.reason;
		return order;
	}
	
	private GameOrder getOrder(SurrenderMessage message) {
		SurrenderOrder order = new SurrenderOrder();
		order.playerId = message.userUUID.contentEquals(API.authKey) ? 1 : 2;
		return order;
	}

	@Override
	protected void postEngineUpdate() {
		if (GameModel.activeGameState != null && GameModel.activeGameState.started && !finished) {
			try {
				for (Player player : GameModel.activeGameState.players) {
					if (player instanceof AiPlayer) {
						List<GameOrder> aiOrders = aiEngine.act((AiPlayer)player, GameModel.activeGameState);
						for (GameOrder order : aiOrders) {
							engine.queue(GameModel.activeGameState, order);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
			EngineUtility.checkVictory(GameModel.activeGameState);
			if (GameModel.activeGameState.winner != 0) {
				Game.stop();
			}
    	}
	}

	@Override
	public void finish() {
		finished = true;
	}

	@Override
	public void cleanup() {
		
	}
}
