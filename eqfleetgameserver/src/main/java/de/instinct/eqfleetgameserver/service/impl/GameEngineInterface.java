package de.instinct.eqfleetgameserver.service.impl;

import java.util.List;

import de.instinct.eqfleetgameserver.service.model.GameSession;
import de.instinct.eqfleetshared.gamelogic.EngineUtility;
import de.instinct.eqfleetshared.gamelogic.EventEngine;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.gamelogic.order.model.GameOrder;

public class GameEngineInterface {
	
	private EventEngine engine;
	
	public GameEngineInterface() {
		engine = new EventEngine();
	}

	public boolean updateGameState(GameSession session) {
		boolean containedValidOrder = false;
		long currentTime = System.currentTimeMillis();
		containedValidOrder = engine.update(session.getGameState(), currentTime - session.getLastUpdateTimeMS());
		session.setLastUpdateTimeMS(currentTime);
		EngineUtility.checkVictory(session.getGameState());
		return containedValidOrder || session.getGameState().winner != 0;
	}

	public void queue(GameState gameState, GameOrder gameOrder) {
		engine.queue(gameState, gameOrder);
	}

	public void queueAll(GameState gameState, List<GameOrder> gameOrders) {
		for (GameOrder gameOrder : gameOrders) {
			queue(gameState, gameOrder);
		}
	}

}
