package de.instinct.engine.combat.unit;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.PlanetData;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.util.EngineUtility;

public class UnitManager {
	
	public static Planet createPlanet(PlanetData planetData) {
		Planet planet = new Planet();
		initializeUnit(planet, planetData.weapon, planetData.defense);
		planet.resourceGenerationSpeed = planetData.resourceGenerationSpeed;
		planet.maxResourceCapacity = planetData.maxResourceCapacity;
		planet.radius = EngineUtility.PLANET_RADIUS;
		return planet;
	}
	
	public static Ship createShip(ShipData shipData) {
		Ship ship = new Ship();
		initializeUnit(ship, shipData.weapon, shipData.defense);
		ship.model = shipData.model;
		ship.type = shipData.type;
		ship.movementSpeed = shipData.movementSpeed;
		ship.cost = shipData.cost;
		ship.radius = 3;
		return ship;
	}
	
	public static void initializeUnit(Unit unit, Weapon weapon, Defense defense) {
		EntityManager.initializeEntity(unit);
		unit.weapon = weapon.clone();
		unit.defense = defense.clone();
		unit.defense.currentShield = defense.shield;
		unit.defense.currentArmor = defense.armor;
	}
	
	public static Unit getClosestInRangeTarget(Unit origin, GameState state) {
        List<Unit> potentialTargets = new ArrayList<>();
        
        Player originPlayer = EngineUtility.getPlayer(state.players, origin.ownerId);
        for (Ship ship : state.ships) {
            Player shipPlayer = EngineUtility.getPlayer(state.players, ship.ownerId);
            if (shipPlayer.teamId != originPlayer.teamId) {
                potentialTargets.add(ship);
            }
        }
        
        for (Planet planet : state.planets) {
            Player planetPlayer = EngineUtility.getPlayer(state.players, planet.ownerId);
            if (planetPlayer.teamId != originPlayer.teamId && planet.defense != null) {
                potentialTargets.add(planet);
            }
    	}
        
        Unit closestTarget = null;
        float closestDistance = Float.MAX_VALUE;
        for (Unit candidate : potentialTargets) {
            float distance = EntityManager.entityDistance(origin, candidate);
            if (distance <= origin.weapon.range && distance < closestDistance) {
                closestDistance = distance;
                closestTarget = candidate;
            }
        }
        return closestTarget;
    }

	public static Unit getUnit(GameState state, int id) {
		for (Ship ship : state.ships) {
			if (ship.id == id) {
				return ship;
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
