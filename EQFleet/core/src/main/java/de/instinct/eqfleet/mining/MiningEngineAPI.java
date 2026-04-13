package de.instinct.eqfleet.mining;

import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.MiningEngine;
import de.instinct.engine.mining.data.MiningGameState;

public class MiningEngineAPI {
	
	private static MiningEngine engine = new MiningEngine();

	public static void initialize(MiningGameState gameState) {
		engine.initialize(gameState);
	}

	public static void update(MiningGameState gameState, long progressionMS) {
		engine.update(gameState, progressionMS);
	}
	
	public static boolean addOrder(MiningGameState gameState, GameOrder order) {
		return gameState.orderData.unprocessedOrders.add(order);
	}

}
