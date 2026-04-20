package de.instinct.eqfleet.mining.driver;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.MiningEngine;
import de.instinct.engine_api.mining.MiningEngineInterface;
import de.instinct.eqfleet.mining.MiningModel;

public abstract class MiningDriver {
	
	protected MiningEngine engine;
	protected MiningEngineInterface engineInterface;
	
	private long lastUpdateTime;
	
	public MiningDriver() {
		engine = new MiningEngine();
		engineInterface = new MiningEngineInterface();
		MiningModel.inputOrders = new ConcurrentLinkedQueue<>();
		initialize();
		lastUpdateTime = System.currentTimeMillis();
	}

	protected abstract void initialize();
	
	public void update() {
		while (!MiningModel.inputOrders.isEmpty()) {
			integrateOrder(MiningModel.inputOrders.poll());
		}
		if (MiningModel.state != null) {
			long now = System.currentTimeMillis();
			long deltaMs = now - lastUpdateTime;
			lastUpdateTime = now;
			engine.update(MiningModel.state, deltaMs);
		}
		updateDriver();
	}
	
	protected abstract void updateDriver();
	
	protected abstract void integrateOrder(GameOrder order);
	
	public abstract void dispose();

}
