package de.instinct.engine.fleet.entity.projectile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.entity.projectile.ProjectileProcessor;
import de.instinct.engine.core.util.VectorUtil;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.unit.Unit;
import de.instinct.engine.fleet.entity.unit.component.Shield;
import de.instinct.engine.fleet.entity.unit.component.Weapon;
import de.instinct.engine.fleet.entity.unit.component.data.types.WeaponType;
import de.instinct.engine.fleet.entity.unit.ship.Ship;
import de.instinct.engine.fleet.entity.unit.turret.Turret;
import de.instinct.engine.fleet.player.FleetPlayer;
import de.instinct.engine.fleet.player.FleetPlayerProcessor;
import de.instinct.engine.fleet.stats.StatCollector;
import de.instinct.engine.fleet.stats.model.PlayerStatistic;
import de.instinct.engine.fleet.stats.model.unit.UnitStatistic;
import de.instinct.engine.fleet.stats.model.unit.component.types.ShieldStatistic;
import de.instinct.engine.fleet.stats.model.unit.component.types.WeaponStatistic;

public class FleetProjectileProcessor extends ProjectileProcessor<FleetProjectile> {
	
	private FleetPlayerProcessor playerProcessor;
	
	private List<ShieldDamageInstance> shieldDamageInstances;
	
	public FleetProjectileProcessor() {
		playerProcessor = new FleetPlayerProcessor();
		
		shieldDamageInstances = new ArrayList<>();
	}

    public void updateFleetProjectiles(FleetGameState state, long deltaTime) {
    	super.updateProjectiles(state.entityData.projectiles, state, deltaTime);
        for (FleetProjectile projectile : state.entityData.projectiles) {
        	updateProjectile(projectile, state, deltaTime);
        }
    }

    private void updateProjectile(FleetProjectile projectile, FleetGameState state, long deltaTime) {
        calculateHit(projectile, state);
    }

	private void calculateHit(FleetProjectile projectile, FleetGameState state) {
    	FleetPlayer projectileOwner = playerProcessor.getFleetPlayer(state.playerData.players, projectile.ownerId);
        for (Ship ship : state.entityData.ships) {
        	if (checkFleetHit(projectile, projectileOwner, ship, state)) {
        		calculateDamage(projectile, ship, state);
        		projectile.flaggedForDestroy = true;
        		break;
        	}
        }
        if (!projectile.flaggedForDestroy) {
        	for (Turret turret : state.entityData.turrets) {
        		if (checkFleetHit(projectile, projectileOwner, turret, state)) {
        			calculateDamage(projectile, turret, state);
            		projectile.flaggedForDestroy = true;
            		break;
            	}
            }
		}
        
    }
	
	private boolean checkFleetHit(FleetProjectile projectile, FleetPlayer projectileOwner, Unit potencialTarget, FleetGameState state) {
		if (super.checkHit(projectile, projectileOwner, potencialTarget, state)) {
			FleetPlayer targetOwner = playerProcessor.getFleetPlayer(state.playerData.players, potencialTarget.ownerId);
	    	if (projectileOwner.teamId != targetOwner.teamId) {
	    		return true;
	    	}
		}
		return false;
	}

	private void calculateDamage(FleetProjectile projectile, Unit target, FleetGameState state) {
        if (projectile.aoeRadius > 0) {
        	FleetPlayer projectileOwner = playerProcessor.getFleetPlayer(state.playerData.players, projectile.ownerId);
        	for (Ship pentencialTargetShip : state.entityData.ships) {
        		double distanceToTarget = super.entityDistance(projectile, pentencialTargetShip);
        		if (distanceToTarget < projectile.aoeRadius) {
        			if (projectileOwner.teamId != playerProcessor.getPlayer(state.playerData.players, pentencialTargetShip.ownerId).teamId) {
        				dealDamage(projectile, pentencialTargetShip, state);
        			}
        		}
        	}
        	for (Turret pentencialTargetTurret : state.entityData.turrets) {
        		double distanceToTarget = super.entityDistance(projectile, pentencialTargetTurret);
        		if (distanceToTarget < projectile.aoeRadius) {
        			if (projectileOwner.teamId != playerProcessor.getPlayer(state.playerData.players, pentencialTargetTurret.ownerId).teamId) {
        				dealDamage(projectile, pentencialTargetTurret, state);
        			}
        		}
        	}
        } else {
            dealDamage(projectile, target, state);
        }
    }

    private void dealDamage(FleetProjectile projectile, Unit target, FleetGameState state) {
    	double remainingDamage = projectile.damage;
    	double finalHullDamage = 0f;
		for (Shield shield : target.shields) {
			if (remainingDamage <= 0) break;
			switch (shield.data.type) {
			case PLASMA:
				double shieldDamage = Math.min(shield.currentStrength, remainingDamage);
				shield.currentStrength -= shieldDamage;
				remainingDamage -= shieldDamage;
				ShieldDamageInstance plasmaDamageInstance = new ShieldDamageInstance();
				plasmaDamageInstance.setShieldId(shield.id);
				plasmaDamageInstance.setDamage(shieldDamage);
				shieldDamageInstances.add(plasmaDamageInstance);
				break;
			case NULLPOINT:
				if (shield.currentStrength >= 1) {
					ShieldDamageInstance nullpointDamageInstance = new ShieldDamageInstance();
					nullpointDamageInstance.setShieldId(shield.id);
					nullpointDamageInstance.setDamage(remainingDamage);
					shieldDamageInstances.add(nullpointDamageInstance);
					remainingDamage = 0f;
					shield.currentStrength--;
				}
				break;
			case GRAVITON:
				//TODO redirect projectiles back
				//change owner of projectile to prevent allies from being hit and allow enemies to be hit
				break;
			}
		}
		
		if (remainingDamage > 0) {
			target.currentHull -= remainingDamage;
			finalHullDamage = remainingDamage;
		}
		
		if (target.currentHull <= 0) {
			finalHullDamage = remainingDamage + target.currentHull;
			target.currentHull = 0;
		}
		
		PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, projectile.ownerId);
		UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(projectile.originModel);
		if (unitStat.getWeaponStatistics() != null) {
			for (WeaponStatistic weaponStat : unitStat.getWeaponStatistics()) {
				if (weaponStat.getId() == projectile.weaponId) {
					weaponStat.setDamageDealt(weaponStat.getDamageDealt() + projectile.damage);
					
					if (target.currentHull <= 0) {
						weaponStat.setKills(weaponStat.getKills() + 1);
					}
				}
			}
		}
		
		PlayerStatistic targetUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, target.ownerId);
		UnitStatistic targetUnitStat = targetUnitOwnerStatistic.getUnit(target.data.model);
		if (targetUnitStat.getHullStatistic() != null) targetUnitStat.getHullStatistic().setDamageTaken(targetUnitStat.getHullStatistic().getDamageTaken() + finalHullDamage);
		if (targetUnitStat.getShieldStatistics() != null) {
			for (ShieldDamageInstance shieldDamageInstance : shieldDamageInstances) {
				for (ShieldStatistic shieldStat : targetUnitStat.getShieldStatistics()) {
					if (shieldStat.getId() == shieldDamageInstance.getShieldId()) {
						shieldStat.setDamageAbsorped(shieldStat.getDamageAbsorped() + shieldDamageInstance.getDamage());
						shieldStat.setDamageInstancesBlocked(shieldStat.getDamageInstancesBlocked() + 1);
					}
				}
			}
		}
		shieldDamageInstances.clear();
	}

	public FleetProjectile createProjectile(Unit origin, int weaponId, Unit target, FleetGameState state) {
        FleetProjectile projectile = new FleetProjectile();;
        Weapon weapon = null;
        for (Weapon w : origin.weapons) {
			if (w.id == weaponId) {
				weapon = w;
				break;
			}
		}
        super.initializeEntity(projectile, state.entityData);
        projectile.ownerId = origin.ownerId;
        projectile.weaponId = weapon.id;
        projectile.originModel = origin.data.model;
        projectile.originId = origin.id;
        projectile.weaponType = weapon.data.type;
		projectile.speed = (float) weapon.data.speed;
		projectile.damage = weapon.data.damage;
		projectile.aoeRadius = weapon.data.aoeRadius;
		projectile.radius = 1f;
		int lifetime = (int)((weapon.data.range / projectile.speed) * 1000f * 1.5f);
		projectile.lifetimeMS = lifetime;
		
        if (weapon.data.type == WeaponType.MISSILE) {
        	Vector2 startPosition = VectorUtil.getTargetPosition(origin.position, target.position, (float) origin.radius);
        	projectile.position = startPosition;
        	projectile.target = target;
			projectile.direction = VectorUtil.getDirection(projectile.position, target.position);
		} else {
        	projectile.position = VectorUtil.getDirectionalTargetPosition(origin.position, VectorUtil.getDirection(origin.position, target.position), (float) origin.radius);
        	projectile.direction = calculateInterceptionDirection(origin, projectile, target, state);
		}
        
        PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, origin.ownerId);
		UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(origin.data.model);
		if (unitStat.getWeaponStatistics() != null) {
			for (WeaponStatistic weaponStat : unitStat.getWeaponStatistics()) {
				if (weaponStat.getId() == weapon.id) {
					weaponStat.setShotsFired(weaponStat.getShotsFired() + 1);
				}
			}
		}
        return projectile;
    }
    
}
