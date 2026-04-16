package de.instinct.engine.mining.entity.asteroid;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.mining.data.MiningGameState;
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
	
	public void createAsteroid(MiningGameState state, Vector2 position, ResourceType resourceType, float resourceAmount) {
		Asteroid asteroid = new Asteroid();
		super.initializeEntity(asteroid, state.entityData);
		asteroid.position = position;
		asteroid.radius = 10f;
		asteroid.maxHealth = 10f;
		asteroid.currentHealth = asteroid.maxHealth;
		asteroid.resourceType = resourceType;
		asteroid.resourceAmount = resourceAmount;
		state.entityData.asteroids.add(asteroid);
	}

	public void damageAsteroid(Asteroid asteroid, MiningPlayerShip ship) {
		asteroid.currentHealth -= ship.weapon.damage;
		if (asteroid.currentHealth <= 0) {
			cargoProcessor.addCargo(ship, asteroid.resourceType, asteroid.resourceAmount);
			asteroid.flaggedForDestroy = true;
		}
	}

}
