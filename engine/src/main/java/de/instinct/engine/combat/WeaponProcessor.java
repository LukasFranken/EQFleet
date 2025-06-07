package de.instinct.engine.combat;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.entity.Unit;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;

public class WeaponProcessor {
    
    private ProjectileProcessor projectileProcessor;
    
    public WeaponProcessor() {
        projectileProcessor = new ProjectileProcessor();
    }
    
    public Projectile fireAtTarget(Unit unit, Unit closestInRangeTarget, long deltaTime) {
        if (unit.currentWeaponCooldown <= deltaTime) {
            long remainingDeltaTime = deltaTime - unit.currentWeaponCooldown;
            unit.currentWeaponCooldown = unit.weapon.cooldown - remainingDeltaTime;
            return projectileProcessor.createProjectileInstance(unit, closestInRangeTarget);
        } else {
            unit.currentWeaponCooldown -= deltaTime;
            return null;
        }
    }
    
    public Unit getClosestInRangeTarget(Unit origin, GameState state, Combat combat) {
        List<Unit> potentialTargets = new ArrayList<>();
        
        Player originPlayer = EngineUtility.getPlayer(state.players, origin.ownerId);
        for (Ship ship : combat.ships) {
            Player shipPlayer = EngineUtility.getPlayer(state.players, ship.ownerId);
            if (shipPlayer.teamId != originPlayer.teamId) {
                potentialTargets.add(ship);
            }
        }
        
        if (origin instanceof Ship) {
        	for (int planetId : combat.planetIds) {
        		Planet planet = EngineUtility.getPlanet(state.planets, planetId);
                Player planetPlayer = EngineUtility.getPlayer(state.players, planet.ownerId);
                if (planetPlayer.teamId != originPlayer.teamId && planet.defense != null) {
                    potentialTargets.add(planet);
                }
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
}
