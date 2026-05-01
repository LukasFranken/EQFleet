package de.instinct.engine.mining;

import de.instinct.engine.core.Engine;
import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.entity.asteroid.AsteroidProcessor;
import de.instinct.engine.mining.entity.projectile.MiningProjectileProcessor;
import de.instinct.engine.mining.entity.ship.MiningPlayerShipProcessor;
import de.instinct.engine.mining.player.MiningPlayerProcessor;

public class MiningEngine extends Engine {
	
	private MiningPlayerShipProcessor playerShipProcessor;
	private MiningPlayerProcessor playerProcessor;
	private MiningProjectileProcessor projectileProcessor;
	private AsteroidProcessor asteroidProcessor;
	
	public MiningEngine() {
		super();
		playerShipProcessor = new MiningPlayerShipProcessor();
		playerProcessor = new MiningPlayerProcessor();
		projectileProcessor = new MiningProjectileProcessor();
		asteroidProcessor = new AsteroidProcessor();
	}

	@Override
	public void initialize(GameState state) {
		MiningGameState miningState = (MiningGameState) state;
		playerShipProcessor.createPlayerShips(miningState);
		asteroidProcessor.createAsteroids(miningState);
		playerProcessor.initializeRecallArea(miningState);
	}

	@Override
	protected void advanceStateTime(GameState state, long deltaTime) {
		MiningGameState miningState = (MiningGameState) state;
		projectileProcessor.updateMiningProjectiles(miningState, deltaTime);
		playerShipProcessor.update(miningState, deltaTime);
		playerProcessor.update(miningState, deltaTime);
		asteroidProcessor.updateAsteroids(miningState, deltaTime);
	}

	@Override
	protected boolean integrateOrder(GameState state, GameOrder order) {
		MiningGameState miningState = (MiningGameState) state;
		if (playerShipProcessor.integrateNewOrder(miningState, order)) return true;
		return false;
	}

}
