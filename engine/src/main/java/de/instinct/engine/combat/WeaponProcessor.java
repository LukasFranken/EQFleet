package de.instinct.engine.combat;

import de.instinct.engine.combat.projectile.ProjectileProcessor;
import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.component.Weapon;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.model.GameState;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.stats.model.unit.UnitStatistic;
import de.instinct.engine.stats.model.unit.component.types.WeaponStatistic;

public class WeaponProcessor {
    
    private ProjectileProcessor projectileProcessor;
    
    public WeaponProcessor() {
        projectileProcessor = new ProjectileProcessor();
    }
    
    public void updateWeapons(GameState state, Unit unit, long deltaTime) {
    	for (Weapon weapon : unit.weapons) {
    		float cooledDownMS = deltaTime;
    		if (weapon.currentCooldown <= deltaTime) {
    			cooledDownMS = weapon.currentCooldown;
    			weapon.currentCooldown = 0;
    		} else {
    			weapon.currentCooldown -= deltaTime;
    		}
    		PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, unit.ownerId);
			UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(unit.data.model);
			if (unitStat.getWeaponStatistics() != null) {
				for (WeaponStatistic weaponStat : unitStat.getWeaponStatistics()) {
					if (weaponStat.getId() == weapon.id) {
						weaponStat.setCooledDownSec(weaponStat.getCooledDownSec() + ((float)cooledDownMS / 1000f));
					}
				}
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
