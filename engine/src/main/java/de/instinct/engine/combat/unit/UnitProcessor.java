package de.instinct.engine.combat.unit;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.combat.DefenseProcessor;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.WeaponProcessor;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.combat.unit.component.Weapon;
import de.instinct.engine.entity.Entity;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.UnitData;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.components.ShieldData;
import de.instinct.engine.model.ship.components.WeaponData;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.stats.model.unit.ShipStatistic;
import de.instinct.engine.stats.model.unit.UnitStatistic;
import de.instinct.engine.util.EngineUtility;

public abstract class UnitProcessor {
	
	private static List<Unit> potentialTargets = new ArrayList<>();
	
	public static void updateUnit(Unit unit, GameState state, long deltaTime) {
		EntityProcessor.updateEntity(unit, state, deltaTime);
		if (unit.currentHull <= 0) {
			unit.flaggedForDestroy = true;
			PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, unit.ownerId);
			UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(unit.data.model);
			if (unitStat instanceof ShipStatistic) ((ShipStatistic)unitStat).getCoreStatistic().setTimesDestroyed(((ShipStatistic)unitStat).getCoreStatistic().getTimesDestroyed() + 1);
			return;
		}
		WeaponProcessor.updateWeapons(state, unit, deltaTime);
		DefenseProcessor.updateDefense(state, unit, deltaTime);
		Unit closestTarget = getClosestTarget(unit, state);
		if (closestTarget != null) WeaponProcessor.aimAtTarget(unit, closestTarget, state, deltaTime);
	}
	
	public static boolean isInCombatRange(Unit unit, GameState state) {
		if (!unit.weapons.isEmpty()) {
			Unit closestTarget = getClosestTarget(unit, state);
			if (closestTarget == null) return false;
			if (EntityManager.entityDistance(unit, closestTarget) <= unit.weapons.get(0).data.range) {
				return true;
			}
		}
		return false;
	}
	
	public static Unit getClosestTarget(Entity origin, GameState state) {
        Player originPlayer = EngineUtility.getPlayer(state.staticData.playerData.players, origin.ownerId);
        addPotentialTargets(state, originPlayer);
        Unit closestTarget = null;
        float closestDistance = Float.MAX_VALUE;
        for (Unit candidate : potentialTargets) {
            float distance = EntityManager.entityDistance(origin, candidate);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = candidate;
            }
        }
        potentialTargets.clear();
        return closestTarget;
	}
	
	private static void addPotentialTargets(GameState state, Player originPlayer) {
		for (Ship ship : state.entityData.ships) {
            Player shipPlayer = EngineUtility.getPlayer(state.staticData.playerData.players, ship.ownerId);
            if (shipPlayer.teamId != originPlayer.teamId) {
                potentialTargets.add(ship);
            }
        }
		
		for (Turret turret : state.entityData.turrets) {
            Player planetPlayer = EngineUtility.getPlayer(state.staticData.playerData.players, turret.ownerId);
            if (planetPlayer.teamId != originPlayer.teamId) {
                potentialTargets.add(turret);
            }
    	}
	}
	
	public static void initializeUnit(Unit unit, UnitData unitData, int planetId, GameState state, boolean payCost) {
		EntityProcessor.initializeEntity(unit, state);
		unit.originPlanetId = planetId;
		unit.data = unitData;
		
		Planet planet = EngineUtility.getPlanet(state.entityData.planets, unit.originPlanetId);
		unit.ownerId = planet.ownerId;
		
		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, unit.ownerId);
        PlayerStatistic playerStat = StatCollector.getPlayer(state.gameUUID, player.id);
		
		if (payCost) { 
			player.currentResources -= unit.data.resourceCost;
	        playerStat.setResourcesUsed(playerStat.getResourcesUsed() + unit.data.resourceCost);
		}
		
		unit.currentHull = unit.data.hullStrength;
		
		unit.shields = new ArrayList<>();
		for (ShieldData shieldData : unit.data.shields) {
			Shield shield = new Shield();
			shield.data = shieldData;
			shield.id = shieldData.id;
			shield.currentStrength = shieldData.strength;
			unit.shields.add(shield);
		}
		
		unit.weapons = new ArrayList<>();
		for (WeaponData weaponData : unit.data.weapons) {
			Weapon weapon = new Weapon();
			weapon.data = weaponData;
			weapon.id = weaponData.id;
			weapon.currentCooldown = 0f;
			unit.weapons.add(weapon);
		}
		
		UnitStatistic unitStat = playerStat.getUnit(unit.data.model);
		unitStat.setTimesBuilt(unitStat.getTimesBuilt() + 1);
	}

}
