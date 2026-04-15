package de.instinct.engine.mining.entity.asteroid;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.mining.data.MiningGameState;

public class AsteroidProcessor extends EntityProcessor {
	
	public void createAsteroid(MiningGameState state, Vector2 position, ResourceType resourceType, float resourceAmount) {
		Asteroid asteroid = new Asteroid();
		super.initializeEntity(asteroid, state.entityData);
		asteroid.position = position;
		asteroid.radius = 20;
		asteroid.maxHealth = 10f;
		asteroid.currentHealth = asteroid.maxHealth;
		asteroid.resourceType = resourceType;
		asteroid.resourceAmount = resourceAmount;
		state.entityData.asteroids.add(asteroid);
	}

}
