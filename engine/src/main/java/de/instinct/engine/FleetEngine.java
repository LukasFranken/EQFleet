package de.instinct.engine;

import de.instinct.engine.combat.CombatProcessor;
import de.instinct.engine.meta.MetaProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.planet.PlanetProcessor;
import de.instinct.engine.player.PlayerProcessor;
import de.instinct.engine.util.VictoryCalculator;

public class FleetEngine {
	
	private static final int UPDATE_INTERVAL_MS = 20;
	
	public static void initialize(GameState state) {
		PlayerProcessor.initialize(state);
		CombatProcessor.initialize(state);
	}

	public static void update(GameState state, long progressionMS) {
		try {
			if (state.started && state.resultData.winner == 0) {
				advanceTime(state, progressionMS);
			    integrateNewOrders(state);
			    VictoryCalculator.checkVictory(state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void integrateNewOrders(GameState state) {
		while (!state.orderData.unprocessedOrders.isEmpty() && state.orderData.unprocessedOrders.peek().processGameTimeStamp <= state.gameTimeMS) {
			GameOrder order = state.orderData.unprocessedOrders.poll();
			if (processOrder(state, order)) {
				order.processGameTimeStamp = state.gameTimeMS;
				state.orderData.processedOrders.add(order);
			}
		}
	}

	private static boolean processOrder(GameState state, GameOrder order) {
		if (CombatProcessor.integrateNewOrder(state, order)) return true;
		if (MetaProcessor.integrateNewOrder(state, order)) return true;
		return false;
	}

	private static void advanceTime(GameState state, long progressionMS) {
		long remainingTime = progressionMS;
		while (remainingTime > 0) {
			long deltaTime = Math.min(UPDATE_INTERVAL_MS, remainingTime);
			remainingTime -= deltaTime;
			MetaProcessor.update(state, deltaTime);
			if (state.pauseData.teamPause == 0 && state.pauseData.resumeCountdownMS <= 0) {
				CombatProcessor.update(state, deltaTime);
				PlanetProcessor.update(state, deltaTime);
			    PlayerProcessor.update(state, deltaTime);
			    state.gameTimeMS += deltaTime;
			}
		}
	}
	
	public static void queue(GameState state, GameOrder order) {
		state.orderData.unprocessedOrders.add(order);
	}
	
}
