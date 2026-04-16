package de.instinct.engine.mining.entity.projectile;

import de.instinct.engine.core.entity.projectile.ProjectileProcessor;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.entity.asteroid.Asteroid;
import de.instinct.engine.mining.entity.asteroid.AsteroidProcessor;
import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.player.MiningPlayer;

public class MiningProjectileProcessor extends ProjectileProcessor<MiningProjectile> {
	
	private AsteroidProcessor asteroidProcessor;
	
	public MiningProjectileProcessor() {
		super();
		asteroidProcessor = new AsteroidProcessor();
	}

	public void updateMiningProjectiles(MiningGameState state, long progressionMS) {
		super.updateProjectiles(state.entityData.projectiles, state, progressionMS);
		 for (MiningProjectile projectile : state.entityData.projectiles) {
			 updateMiningProjectile(state, projectile, progressionMS);
		 }
	}

	private void updateMiningProjectile(MiningGameState state, MiningProjectile projectile, long progressionMS) {
		for (Asteroid asteroid : state.entityData.asteroids) {
			if (checkHit(projectile, asteroid)) {
				asteroidProcessor.damageAsteroid(asteroid, projectile.ship);
				projectile.flaggedForDestroy = true;
			}
		}
	}
	
	public void createProjectile(MiningGameState state, MiningPlayer shipOwner, MiningPlayerShip ship) {
		MiningProjectile projectile = new MiningProjectile();
		super.initializeEntity(projectile, state.entityData);
		projectile.ship = ship;
		projectile.lifetimeMS = ship.weapon.lifetimeMS;
		projectile.position = ship.position.cpy();
		projectile.direction = ship.direction.cpy();
		projectile.speed = 300;
		state.entityData.projectiles.add(projectile);
	}

}
