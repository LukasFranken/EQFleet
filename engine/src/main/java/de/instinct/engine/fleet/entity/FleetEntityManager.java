package de.instinct.engine.fleet.entity;

import de.instinct.engine.core.entity.Entity;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.entity.projectile.FleetProjectile;
import de.instinct.engine.fleet.entity.unit.ship.Ship;

public class FleetEntityManager {
	
	public static Entity getEntity(FleetGameState state, int id) {
		for (Ship ship : state.entityData.ships) {
			if (ship.id == id) {
				return ship;
			}
		}
		for (FleetProjectile projectile : state.entityData.projectiles) {
			if (projectile.id == id) {
				return projectile;
			}
		}
		for (Planet planet : state.entityData.planets) {
			if (planet.id == id) {
				return planet;
			}
		}
		return null;
	}

}
