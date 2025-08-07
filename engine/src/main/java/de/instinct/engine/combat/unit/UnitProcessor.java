package de.instinct.engine.combat.unit;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.combat.DefenseProcessor;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.WeaponProcessor;
import de.instinct.engine.entity.Entity;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.UnitData;
import de.instinct.engine.model.planet.Planet;
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
		if (unit.defense.currentArmor <= 0) {
			unit.flaggedForDestroy = true;
			return;
		}
		weaponProcessor.updateWeapon(unit.weapon, deltaTime);
		defenseProcessor.updateDefense(unit.defense, deltaTime);
		Unit closestInRangeTarget = getClosestInRangeTarget(unit, unit.weapon.range, state);
		if (closestInRangeTarget != null) {
			weaponProcessor.fireAtTarget(unit, closestInRangeTarget, state, deltaTime);
		}
	}
	
	public static Unit getClosestInRangeTarget(Entity origin, float range, GameState state) {
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
            if (planetPlayer.teamId != originPlayer.teamId && turret.defense != null) {
                potentialTargets.add(turret);
            }
    	}
        
        Unit closestTarget = null;
        float closestDistance = Float.MAX_VALUE;
        for (Unit candidate : potentialTargets) {
            float distance = EntityManager.entityDistance(origin, candidate);
            if (distance <= range && distance < closestDistance) {
                closestDistance = distance;
                closestTarget = candidate;
            }
        }
        return closestTarget;
    }
	
	public void initializeUnit(Unit unit, UnitData unitData, int planetId, GameState state, boolean payCost) {
		super.initializeEntity(unit, state);
		unit.planetId = planetId;
		unit.model = unitData.model;
		unit.cost = unitData.cost;
		Planet planet = EngineUtility.getPlanet(state.planets, unit.planetId);
		unit.ownerId = planet.ownerId;
		
		if (payCost) {
	        planet.currentResources -= unit.cost;
	        Player player = EngineUtility.getPlayer(state.players, unit.ownerId);
	        player.currentCommandPoints -= unitData.commandPointsCost;
		}
        
		if (unitData.weapon != null) unit.weapon = unitData.weapon.clone();
		if (unitData.defense != null) {
			unit.defense = unitData.defense.clone();
			unit.defense.currentShield = unit.defense.shield;
			unit.defense.currentArmor = unit.defense.armor;
		}
	}

}
