package de.instinct.engine.combat;

import de.instinct.engine.entity.Unit;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;

public class PlanetCombatProcessor {
    
    private WeaponProcessor weaponProcessor;
    
    public PlanetCombatProcessor() {
        weaponProcessor = new WeaponProcessor();
    }
    
    public void updatePlanets(Combat combat, GameState state, long deltaTime) {
        for (int planetId : combat.planetIds) {
        	Planet planet = EngineUtility.getPlanet(state.planets, planetId);
        	if (planet.currentArmor <= 0) {
    			planet.defense = null;
    			planet.weapon = null;
            }
            updatePlanet(planet, combat, state, deltaTime);
        }
    }
    
    private void updatePlanet(Planet planet, Combat combat, GameState state, long deltaTime) {
        if (planet == null || planet.weapon == null) {
            return;
        }
        Unit target = weaponProcessor.getClosestInRangeTarget(planet, state, combat);
        if (target != null) {
            Projectile newProjectile = weaponProcessor.fireAtTarget(planet, target, deltaTime);
			if (newProjectile != null) {
				combat.projectiles.add(newProjectile);
			}
        }
    }

	public void resetPlanetCooldowns(GameState state, Combat combat) {
    	for (int planetId : combat.planetIds) {
    		Planet planet1 = EngineUtility.getPlanet(state.planets, planetId);
    		if (!isPlanetStillInCombat(state, planetId)) {
                planet1.currentWeaponCooldown = 0;
            }
		}
    }

    private boolean isPlanetStillInCombat(GameState state, int planetId) {
        for (Combat activeCombat : state.activeCombats) {
            if (activeCombat.planetIds.contains(planetId)) {
                return true;
            }
        }
        return false;
    }
}
