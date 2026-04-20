package de.instinct.engine.core;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.meta.MetaProcessor;
import de.instinct.engine.core.order.GameOrder;

public abstract class Engine {
	
	private final int UPDATE_INTERVAL_MS = 20;
	
	private MetaProcessor metaProcessor;
	
	public Engine() {
		metaProcessor = new MetaProcessor();
	}
	
	public abstract void initialize(GameState state);
	
	public void update(GameState state, long progressionMS) {
		try {
			if (state.metaData.started) {
				advanceTime(state, progressionMS);
			    integrateNewOrders(state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void advanceTime(GameState state, long progressionMS) {
		long remainingTime = progressionMS;
		while (remainingTime > 0) {
			long deltaTime = Math.min(UPDATE_INTERVAL_MS, remainingTime);
			remainingTime -= deltaTime;
			metaProcessor.update(state, deltaTime);
			if (state.metaData.pauseData.teamPause == 0 && state.metaData.pauseData.resumeCountdownMS <= 0) {
				advanceStateTime(state, deltaTime);
				state.metaData.gameTimeMS += deltaTime;
			}
		}
	}
	
	protected abstract void advanceStateTime(GameState state, long deltaTime);

	private void integrateNewOrders(GameState state) {
		while (!state.orderData.unprocessedOrders.isEmpty() && state.orderData.unprocessedOrders.peek().processGameTimeStamp <= state.metaData.gameTimeMS) {
			GameOrder order = state.orderData.unprocessedOrders.poll();
			if (integrateOrder(state, order)) {
				order.processGameTimeStamp = state.metaData.gameTimeMS;
				state.orderData.processedOrders.add(order);
			}
		}
	}
	
	protected abstract boolean integrateOrder(GameState state, GameOrder order);

	public void queue(GameState state, GameOrder order) {
		state.orderData.unprocessedOrders.add(order);
	}

}
