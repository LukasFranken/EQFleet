package de.instinct.engine.combat;

import de.instinct.engine.combat.projectile.ProjectileProcessor;
import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.component.Weapon;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.model.GameState;

public class WeaponProcessor {
    
    private ProjectileProcessor projectileProcessor;
    
    public WeaponProcessor() {
        projectileProcessor = new ProjectileProcessor();
    }
    
    public void updateWeapons(Unit unit, long deltaTime) {
    	for (Weapon weapon : unit.weapons) {
    		if (weapon.currentCooldown <= deltaTime) {
    			weapon.currentCooldown = 0;
    		} else {
    			weapon.currentCooldown -= deltaTime;
    		}
		}
	}
    
    public void aimAtTarget(Unit unit, Unit closestTarget, GameState state, long deltaTime) {
        for (Weapon weapon : unit.weapons) {
        	if (weapon.currentCooldown == 0) {
                if (EntityManager.entityDistance(unit, closestTarget) <= weapon.data.range) {
                	state.projectiles.add(projectileProcessor.createProjectile(unit, weapon.id, closestTarget, state));
                    weapon.currentCooldown = weapon.data.cooldown;
                }
            }
        }
    }
    
}
