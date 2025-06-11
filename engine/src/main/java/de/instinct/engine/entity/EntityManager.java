package de.instinct.engine.entity;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.VectorUtil;

public class EntityManager {
	
	private static int entityId;
	
	public static void init() {
		entityId = 0;
	}
	
	public static void initializeEntity(Entity newEntity) {
		newEntity.id = entityId++;
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
