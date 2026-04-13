package de.instinct.engine.fleet.entity.unit.component;

import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.projectile.FleetProjectileProcessor;
import de.instinct.engine.fleet.entity.unit.Unit;
import de.instinct.engine.fleet.stats.StatCollector;
import de.instinct.engine.fleet.stats.model.PlayerStatistic;
import de.instinct.engine.fleet.stats.model.unit.UnitStatistic;
import de.instinct.engine.fleet.stats.model.unit.component.types.WeaponStatistic;

public class WeaponProcessor extends EntityProcessor {
	
	private FleetProjectileProcessor projectileProcessor;
	
	public WeaponProcessor() {
		projectileProcessor = new FleetProjectileProcessor();
	}
    
    public void updateWeapons(FleetGameState state, Unit unit, long deltaTime) {
    	for (Weapon weapon : unit.weapons) {
    		double cooledDownMS = deltaTime;
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
    
    public void aimAtTarget(Unit unit, Unit closestTarget, FleetGameState state, long deltaTime) {
        for (Weapon weapon : unit.weapons) {
        	if (weapon.currentCooldown == 0) {
                if (super.entityDistance(unit, closestTarget) <= weapon.data.range) {
                	state.entityData.projectiles.add(projectileProcessor.createProjectile(unit, weapon.id, closestTarget, state));
                    weapon.currentCooldown = weapon.data.cooldown;
                }
            }
        }
    }
    
}
