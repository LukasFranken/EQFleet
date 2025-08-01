package de.instinct.engine.entity;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.VectorUtil;

public class EntityManager {
	
	public static void initializeEntity(Entity newEntity, GameState state) {
		newEntity.id = state.entityCounter;
		state.entityCounter++;
	}
	
	public static float entityDistance(Entity origin, Entity target) {
		return VectorUtil.dst(origin.position, target.position) - origin.radius - target.radius;
	}
	
	public static Entity getEntity(GameState state, int id) {
		for (Ship ship : state.ships) {
			if (ship.id == id) {
				return ship;
			}
		}
		for (Projectile projectile : state.projectiles) {
			if (projectile.id == id) {
				return projectile;
			}
		}
		for (Planet planet : state.planets) {
			if (planet.id == id) {
				return planet;
			}
		}
		return null;
	}

}
