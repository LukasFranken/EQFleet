package de.instinct.engine.entity;

import de.instinct.engine.combat.Combat;
import de.instinct.engine.combat.Projectile;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.PlanetData;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine.util.VectorUtil;

public class EntityManager {
	
	private static int entityId;
	
	public static void init() {
		entityId = 0;
	}
	
	public static Projectile createProjectile(Weapon weapon) {
		Projectile projectile = new Projectile();
		projectile.id = entityId++;
		projectile.weaponType = weapon.type;
		projectile.movementSpeed = weapon.speed;
		projectile.damage = weapon.damage;
		projectile.radius = 0;
		return projectile;
	}
	
	public static Planet createPlanet(PlanetData planetData) {
		Planet planet = new Planet();
		initializeUnit(planet, planetData.weapon, planetData.defense);
		planet.id = entityId++;
		planet.resourceGenerationSpeed = planetData.resourceGenerationSpeed;
		planet.maxResourceCapacity = planetData.maxResourceCapacity;
		planet.radius = EngineUtility.PLANET_RADIUS;
		return planet;
	}
	
	public static Ship createShip(ShipData shipData) {
		Ship ship = new Ship();
		initializeUnit(ship, shipData.weapon, shipData.defense);
		ship.id = entityId++;
		ship.model = shipData.model;
		ship.type = shipData.type;
		ship.movementSpeed = shipData.movementSpeed;
		ship.cost = shipData.cost;
		ship.radius = 0;
		return ship;
	}
	
	private static void initializeUnit(Unit unit, Weapon weapon, Defense defense) {
		unit.weapon = weapon;
		unit.defense = defense;
		unit.currentShield = defense.shield;
		unit.currentArmor = defense.armor;
	}
	
	public static float entityDistance(Entity origin, Entity target) {
		return VectorUtil.dst(origin.position, target.position) - origin.radius - target.radius;
	}
	
	public static Entity getEntity(GameState state, int id) {
		for (Combat combat : state.activeCombats) {
			for (Ship ship : combat.ships) {
				if (ship.id == id) {
					return ship;
				}
			}
			for (Projectile projectile : combat.projectiles) {
				if (projectile.id == id) {
					return projectile;
				}
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
