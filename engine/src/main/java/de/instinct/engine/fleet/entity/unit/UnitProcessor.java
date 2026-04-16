package de.instinct.engine.fleet.entity.unit;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.entity.unit.component.DefenseProcessor;
import de.instinct.engine.fleet.entity.unit.component.Shield;
import de.instinct.engine.fleet.entity.unit.component.Weapon;
import de.instinct.engine.fleet.entity.unit.component.WeaponProcessor;
import de.instinct.engine.fleet.entity.unit.component.data.ShieldData;
import de.instinct.engine.fleet.entity.unit.component.data.WeaponData;
import de.instinct.engine.fleet.entity.unit.data.UnitData;
import de.instinct.engine.fleet.entity.unit.ship.Ship;
import de.instinct.engine.fleet.entity.unit.turret.Turret;
import de.instinct.engine.fleet.player.FleetPlayer;
import de.instinct.engine.fleet.player.FleetPlayerProcessor;
import de.instinct.engine.fleet.stats.StatCollector;
import de.instinct.engine.fleet.stats.model.PlayerStatistic;
import de.instinct.engine.fleet.stats.model.unit.ShipStatistic;
import de.instinct.engine.fleet.stats.model.unit.UnitStatistic;

public class UnitProcessor extends EntityProcessor {
	
	protected FleetPlayerProcessor playerProcessor;
	protected WeaponProcessor weaponProcessor;
	protected DefenseProcessor defenseProcessor;
	
	private List<Unit> potentialTargets;
	
	public UnitProcessor() {
		super();
		playerProcessor = new FleetPlayerProcessor();
		weaponProcessor = new WeaponProcessor();
		defenseProcessor = new DefenseProcessor();
		
		potentialTargets = new ArrayList<>();
	}
	
	public void updateUnit(Unit unit, FleetGameState state, long deltaTime) {
		super.updateEntity(unit, state, deltaTime);
		if (unit.currentHull <= 0) {
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
	
	public boolean isInCombatRange(Unit unit, FleetGameState state) {
		if (!unit.weapons.isEmpty()) {
			Unit closestTarget = getClosestTarget(unit, state);
			if (closestTarget == null) return false;
			if (super.entityDistance(unit, closestTarget) <= unit.weapons.get(0).data.range) {
				return true;
			}
		}
		return false;
	}
	
	public Unit getClosestTarget(Unit origin, FleetGameState state) {
        Player originPlayer = playerProcessor.getPlayer(state.playerData.players, origin.ownerId);
        addPotentialTargets(state, originPlayer);
        Unit closestTarget = null;
        float closestDistance = Float.MAX_VALUE;
        for (Unit candidate : potentialTargets) {
        	float distance = super.entityDistance(origin, candidate);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = candidate;
            }
        }
        potentialTargets.clear();
        return closestTarget;
	}
	
	private void addPotentialTargets(FleetGameState state, Player originPlayer) {
		for (Ship ship : state.entityData.ships) {
            Player shipPlayer = playerProcessor.getPlayer(state.playerData.players, ship.ownerId);
            if (shipPlayer.teamId != originPlayer.teamId) {
                potentialTargets.add(ship);
            }
        }
		
		for (Turret turret : state.entityData.turrets) {
            Player planetPlayer = playerProcessor.getPlayer(state.playerData.players, turret.ownerId);
            if (planetPlayer.teamId != originPlayer.teamId) {
                potentialTargets.add(turret);
            }
    	}
	}
	
	public void initializeUnit(Unit unit, UnitData unitData, Planet planet, FleetGameState state, boolean payCost) {
		super.initializeEntity(unit, state.entityData);
		unit.originPlanetId = planet.id;
		unit.data = unitData;
		unit.ownerId = planet.ownerId;
		
		FleetPlayer player = playerProcessor.getFleetPlayer(state.playerData.players, unit.ownerId);
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
	
	public Unit getUnit(FleetGameState state, int id) {
		for (Ship ship : state.entityData.ships) {
			if (ship.id == id) {
				return ship;
			}
		}
		for (Turret turret : state.entityData.turrets) {
			if (turret.id == id) {
				return turret;
			}
		}
		System.out.println("couldnt find unit with id " + id);
		System.out.println("ships:");
		for (Ship ship : state.entityData.ships) {
			System.out.println(ship.id);
		}
		System.out.println("turrets:");
		for (Turret turret : state.entityData.turrets) {
			System.out.println(turret.id);
		}
		return null;
	}

}
