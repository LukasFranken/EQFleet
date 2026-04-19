package de.instinct.engine.mining;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.Engine;
import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.entity.asteroid.AsteroidProcessor;
import de.instinct.engine.mining.entity.asteroid.ResourceType;
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
		
		asteroidProcessor.createAsteroid(miningState, new Vector2(500, 500), ResourceType.IRON, 2);
		asteroidProcessor.createAsteroid(miningState, new Vector2(0, 500), ResourceType.CARBON, 5);
		asteroidProcessor.createAsteroid(miningState, new Vector2(500, 0), ResourceType.SILICON, 3);
		asteroidProcessor.createAsteroid(miningState, new Vector2(-500, 0), ResourceType.GOLD, 3);
		asteroidProcessor.createAsteroid(miningState, new Vector2(-500, -500), ResourceType.URANIUM, 1);
		asteroidProcessor.createAsteroid(miningState, new Vector2(-500, 500), ResourceType.ALUMINUM, 1);
		asteroidProcessor.createAsteroid(miningState, new Vector2(-1000, 0), ResourceType.THORIUM, 1);
		asteroidProcessor.createAsteroid(miningState, new Vector2(0, -1000), ResourceType.XENON, 1);
		asteroidProcessor.createAsteroid(miningState, new Vector2(-1000, -1000), ResourceType.SELENIUM, 1);
		asteroidProcessor.createAsteroid(miningState, new Vector2(-1000, -500), ResourceType.EQUILIBRIUM, 1);
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
