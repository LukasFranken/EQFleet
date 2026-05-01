package de.instinct.engine.mining.entity.asteroid;

import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.data.map.node.MiningMapNode;
import de.instinct.engine.mining.data.map.node.types.AsteroidMapNode;
import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.entity.ship.cargo.MiningCargoProcessor;

public class AsteroidProcessor extends EntityProcessor {
	
	private MiningCargoProcessor cargoProcessor;
	
	public AsteroidProcessor() {
		super();
		cargoProcessor = new MiningCargoProcessor();
	}
	
	public void updateAsteroids(MiningGameState state, long progressionMS) {
		for (Asteroid asteroid : state.entityData.asteroids) {
			super.updateEntity(asteroid, state, progressionMS);
		}
		super.removeDestroyed(state.entityData.asteroids.iterator());
	}
	
	public void createAsteroid(MiningGameState state, AsteroidMapNode asteroidNode) {
		Asteroid asteroid = new Asteroid();
		super.initializeEntity(asteroid, state.entityData);
		asteroid.position = asteroidNode.position;
		asteroid.radius = asteroidNode.radius;
		asteroid.maxHealth = asteroidNode.health;
		asteroid.currentHealth = asteroid.maxHealth;
		asteroid.resourceType = asteroidNode.resourceType;
		asteroid.resourceAmount = asteroidNode.value;
		state.entityData.asteroids.add(asteroid);
	}

	public void damageAsteroid(Asteroid asteroid, MiningPlayerShip ship) {
		asteroid.currentHealth -= ship.weapon.damage;
		if (asteroid.currentHealth <= 0) {
			cargoProcessor.addCargo(ship, asteroid.resourceType, asteroid.resourceAmount);
			asteroid.flaggedForDestroy = true;
		}
	}

	public void createAsteroids(MiningGameState miningState) {
		for (MiningMapNode node : miningState.map.nodes) {
			if (node instanceof AsteroidMapNode) {
				createAsteroid(miningState, (AsteroidMapNode) node);
			}
		}
	}

}
