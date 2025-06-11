package de.instinct.engine.combat;

import de.instinct.engine.combat.projectile.ProjectileProcessor;
import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.ship.Weapon;

public class WeaponProcessor {
    
    private ProjectileProcessor projectileProcessor;
    
    public WeaponProcessor() {
        projectileProcessor = new ProjectileProcessor();
    }
    
    public void updateWeapon(Weapon weapon, long deltaTime) {
		if (weapon.currentCooldown <= deltaTime) {
			weapon.currentCooldown = 0;
		} else {
			weapon.currentCooldown -= deltaTime;
		}
	}
    
    public void fireAtTarget(Unit unit, Unit closestInRangeTarget, GameState state, long deltaTime) {
        if (unit.weapon.currentCooldown == 0) {
            state.projectiles.add(projectileProcessor.createProjectile(unit, closestInRangeTarget, state));
            unit.weapon.currentCooldown = unit.weapon.cooldown;
        }
    }
    
}
