package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.UnitManager;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;

public class PlanetCombatProcessor {
    
    private WeaponProcessor weaponProcessor;
    
    public PlanetCombatProcessor() {
        weaponProcessor = new WeaponProcessor();
    }
    
    public void updatePlanets(GameState state, long deltaTime) {
        for (Planet planet : state.planets) {
        	if (planet.defense != null && planet.defense.currentArmor <= 0) {
    			planet.defense = null;
    			planet.weapon = null;
            }
            updatePlanet(planet, state, deltaTime);
        }
    }
    
    private void updatePlanet(Planet planet, GameState state, long deltaTime) {
        if (planet == null || planet.weapon == null) {
            return;
        }
        weaponProcessor.updateWeapon(planet.weapon, deltaTime);
        Unit target = UnitManager.getClosestInRangeTarget(planet, planet.weapon.range, state);
        if (target != null) {
            weaponProcessor.fireAtTarget(planet, target, state, deltaTime);
        }
    }

}
