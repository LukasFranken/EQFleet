package de.instinct.engine;

import de.instinct.engine.combat.CombatProcessor;
import de.instinct.engine.meta.MetaProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.planet.PlanetProcessor;
import de.instinct.engine.player.PlayerProcessor;
import de.instinct.engine.util.VictoryCalculator;

public class FleetEngine {
	
	private final int UPDATE_INTERVAL_MS = 20;
	
	private PlanetProcessor planetProcessor;
	private PlayerProcessor playerProcessor;
	private CombatProcessor combatProcessor;
	private MetaProcessor metaProcessor;
	
	private boolean updateContainedValidOrder;
	
	public void initialize() {
		planetProcessor = new PlanetProcessor();
		playerProcessor = new PlayerProcessor();
		combatProcessor = new CombatProcessor();
		metaProcessor = new MetaProcessor();
	}

	public void update(GameState state, long progressionMS) {
		try {
			if (state.started && state.winner == 0) {
				advanceTime(state, progressionMS);
			    integrateNewOrders(state);
			    VictoryCalculator.checkVictory(state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void integrateNewOrders(GameState state) {
		updateContainedValidOrder = false;
		while (!state.unprocessedOrders.isEmpty()) {
			GameOrder order = state.unprocessedOrders.poll();
			if (processOrder(state, order)) {
				order.orderId = state.orderCounter++;
				order.acceptedTimeMS = state.gameTimeMS;
				state.orders.add(order);
				updateContainedValidOrder = true;
			}
		}
	}

	private boolean processOrder(GameState state, GameOrder order) {
		if (combatProcessor.integrateNewOrder(state, order)) return true;
		if (metaProcessor.integrateNewOrder(state, order)) return true;
		return false;
	}

	private void advanceTime(GameState state, long progressionMS) {
		long remainingTime = progressionMS;
		while (remainingTime > 0) {
			long deltaTime = Math.min(UPDATE_INTERVAL_MS, remainingTime);
			remainingTime -= deltaTime;
			metaProcessor.update(state, deltaTime);
			if (state.pauseData.teamPause == 0 && state.pauseData.resumeCountdownMS <= 0) {
				combatProcessor.update(state, deltaTime);
			    planetProcessor.update(state, deltaTime);
			    playerProcessor.update(state, deltaTime);
			    state.gameTimeMS += deltaTime;
			}
		}
	}
	
	public void queue(GameState state, GameOrder order) {
		state.unprocessedOrders.add(order);
	}
	
	public boolean containedValidOrders() {
		return updateContainedValidOrder;
	}
	
}
