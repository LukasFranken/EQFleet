package de.instinct.engine.combat.unit;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.combat.DefenseProcessor;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.WeaponProcessor;
import de.instinct.engine.combat.unit.component.Hull;
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

public abstract class UnitProcessor extends EntityProcessor {
	
	private WeaponProcessor weaponProcessor;
	private DefenseProcessor defenseProcessor;
	
	public UnitProcessor() {
		super();
		this.weaponProcessor = new WeaponProcessor();
		this.defenseProcessor = new DefenseProcessor();
	}
	
	public void updateUnit(Unit unit, GameState state, long deltaTime) {
		super.updateEntity(unit, state, deltaTime);
		if (unit.hull.currentStrength <= 0) {
			unit.flaggedForDestroy = true;
			PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, unit.ownerId);
			UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(unit.data.model);
			if (unitStat instanceof ShipStatistic) ((ShipStatistic)unitStat).getCoreStatistic().setTimesDestroyed(((ShipStatistic)unitStat).getCoreStatistic().getTimesDestroyed() + 1);
			return;
		}
		weaponProcessor.updateWeapons(state, unit, deltaTime);
		defenseProcessor.updateDefense(state, unit, deltaTime);
		Unit closestTarget = getClosestTarget(unit, state);
		if (closestTarget != null) weaponProcessor.aimAtTarget(unit, closestTarget, state, deltaTime);
	}
	
	public boolean isInCombatRange(Unit unit, GameState state) {
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
		List<Unit> potentialTargets = new ArrayList<>();
        
        Player originPlayer = EngineUtility.getPlayer(state.players, origin.ownerId);
        for (Ship ship : state.ships) {
            Player shipPlayer = EngineUtility.getPlayer(state.players, ship.ownerId);
            if (shipPlayer.teamId != originPlayer.teamId) {
                potentialTargets.add(ship);
            }
        }
        
        for (Turret turret : state.turrets) {
            Player planetPlayer = EngineUtility.getPlayer(state.players, turret.ownerId);
            if (planetPlayer.teamId != originPlayer.teamId && turret.hull != null) {
                potentialTargets.add(turret);
            }
    	}
        
        Unit closestTarget = null;
        float closestDistance = Float.MAX_VALUE;
        for (Unit candidate : potentialTargets) {
            float distance = EntityManager.entityDistance(origin, candidate);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = candidate;
            }
        }
        return closestTarget;
	}
	
	public void initializeUnit(Unit unit, UnitData unitData, int planetId, GameState state, boolean payCost) {
		super.initializeEntity(unit, state);
		unit.originPlanetId = planetId;
		unit.data = unitData;
		
		Planet planet = EngineUtility.getPlanet(state.planets, unit.originPlanetId);
		unit.ownerId = planet.ownerId;
		
		Player player = EngineUtility.getPlayer(state.players, unit.ownerId);
        PlayerStatistic playerStat = StatCollector.getPlayer(state.gameUUID, player.id);
		
		if (payCost) { 
	        planet.currentResources -= unit.data.resourceCost;
	        player.currentCommandPoints -= unit.data.cpCost;
	        
	        playerStat.setResourcesUsed(playerStat.getResourcesUsed() + unit.data.resourceCost);
			playerStat.setCpUsed(playerStat.getCpUsed() + unit.data.cpCost);
		}
		
		unit.hull = new Hull();
		unit.hull.currentStrength = unit.data.hull.strength;
		unit.hull.data = unit.data.hull;
		
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
