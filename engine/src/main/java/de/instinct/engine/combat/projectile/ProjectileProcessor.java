package de.instinct.engine.combat.projectile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.projectile.model.ShieldDamageInstance;
import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.UnitProcessor;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.combat.unit.component.Weapon;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.stats.model.unit.UnitStatistic;
import de.instinct.engine.stats.model.unit.component.types.ShieldStatistic;
import de.instinct.engine.stats.model.unit.component.types.WeaponStatistic;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine.util.VectorUtil;

public class ProjectileProcessor extends EntityProcessor {

    public void updateProjectiles(GameState state, long deltaTime) {
        for (Projectile projectile : state.entityData.projectiles) {
        	updateProjectile(projectile, state, deltaTime);
        }
        super.removeDestroyed(state.entityData.projectiles.iterator());
    }

    private void updateProjectile(Projectile projectile, GameState state, long deltaTime) {
    	super.updateEntity(projectile, state, deltaTime);
        calculateMovement(projectile, state, deltaTime);
        calculateHit(projectile, state);
        if (projectile.elapsedMS >= projectile.lifetimeMS) {
			projectile.flaggedForDestroy = true;
		}
    }

    private void calculateMovement(Projectile projectile, GameState state, float deltaTime) {
    	if (projectile.lifetimeMS < projectile.elapsedMS + deltaTime) {
			deltaTime = projectile.lifetimeMS - projectile.elapsedMS;
			projectile.elapsedMS = projectile.lifetimeMS;
		} else {
			projectile.elapsedMS += deltaTime;
		}
    	float distanceTraveled = projectile.movementSpeed * ((float) deltaTime / 1000f);
        Vector2 targetPosition = projectile.position;
        if (projectile instanceof HomingProjectile) {
        	HomingProjectile homingProjectile = (HomingProjectile) projectile;
        	Unit target = (Unit) EntityManager.getEntity(state, homingProjectile.targetId);
        	if (target == null) {
        		target = findNewTarget(homingProjectile, state);
        	}
        	if (target == null) {
        		targetPosition = VectorUtil.getDirectionalTargetPosition(projectile.position, homingProjectile.lastDirection, distanceTraveled);
        	} else {
        		targetPosition = VectorUtil.getTargetPosition(projectile.position, target.position, distanceTraveled);
        		homingProjectile.lastDirection = VectorUtil.getDirection(projectile.position, target.position);
        	}
        }
		if (projectile instanceof DirectionalProjectile) {
			DirectionalProjectile directionalProjectile = (DirectionalProjectile) projectile;
			targetPosition = VectorUtil.getDirectionalTargetPosition(projectile.position, directionalProjectile.direction, distanceTraveled);
		}
        projectile.position = targetPosition;
    }

    private Unit findNewTarget(HomingProjectile projectile, GameState state) {
		return UnitProcessor.getClosestTarget(projectile, state);
	}

	private void calculateHit(Projectile projectile, GameState state) {
    	Player projectileOwner = EngineUtility.getPlayer(state.staticData.playerData.players, projectile.ownerId);
        for (Ship ship : state.entityData.ships) {
        	if (checkHit(projectile, projectileOwner, ship, state)) {
        		calculateDamage(projectile, ship, state);
        		projectile.flaggedForDestroy = true;
        		break;
        	}
        }
        if (!projectile.flaggedForDestroy) {
        	for (Turret turret : state.entityData.turrets) {
        		if (checkHit(projectile, projectileOwner, turret, state)) {
        			calculateDamage(projectile, turret, state);
            		projectile.flaggedForDestroy = true;
            		break;
            	}
            }
		}
        
    }

    private boolean checkHit(Projectile projectile, Player projectileOwner, Unit potencialTarget, GameState state) {
    	float distanceToTarget = EntityManager.entityDistance(projectile, potencialTarget);
    	if (distanceToTarget <= 0) {
    		Player targetOwner = EngineUtility.getPlayer(state.staticData.playerData.players, potencialTarget.ownerId);
        	if (projectileOwner.teamId != targetOwner.teamId && potencialTarget.hull != null) {
        		return true;
        	}
    	}
    	return false;
	}

	private void calculateDamage(Projectile projectile, Unit target, GameState state) {
        if (projectile.aoeRadius > 0) {
        	Player projectileOwner = EngineUtility.getPlayer(state.staticData.playerData.players, projectile.ownerId);
        	for (Ship pentencialTargetShip : state.entityData.ships) {
        		float distanceToTarget = EntityManager.entityDistance(projectile, pentencialTargetShip);
        		if (distanceToTarget < projectile.aoeRadius) {
        			if (projectileOwner.teamId != EngineUtility.getPlayer(state.staticData.playerData.players, pentencialTargetShip.ownerId).teamId) {
        				if (pentencialTargetShip.hull != null) {
        					dealDamage(projectile, pentencialTargetShip, state);
        				}
        			}
        		}
        	}
        	for (Turret pentencialTargetTurret : state.entityData.turrets) {
        		float distanceToTarget = EntityManager.entityDistance(projectile, pentencialTargetTurret);
        		if (distanceToTarget < projectile.aoeRadius) {
        			if (projectileOwner.teamId != EngineUtility.getPlayer(state.staticData.playerData.players, pentencialTargetTurret.ownerId).teamId) {
        				if (pentencialTargetTurret.hull != null) {
        					dealDamage(projectile, pentencialTargetTurret, state);
        				}
        			}
        		}
        	}
        } else {
            dealDamage(projectile, target, state);
        }
    }

    private void dealDamage(Projectile projectile, Unit target, GameState state) {
    	float remainingDamage = projectile.damage;
		
    	List<ShieldDamageInstance> shieldDamageInstances = new ArrayList<>();
    	
    	float finalHullDamage = 0f;
		for (Shield shield : target.shields) {
			if (remainingDamage <= 0) break;
			switch (shield.data.type) {
			case PLASMA:
				float shieldDamage = Math.min(shield.currentStrength, remainingDamage);
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
				break;
			}
		}
		
		if (remainingDamage > 0) {
			target.hull.currentStrength -= remainingDamage;
			finalHullDamage = remainingDamage;
		}
		
		if (target.hull.currentStrength <= 0) {
			finalHullDamage = remainingDamage + target.hull.currentStrength;
			target.hull.currentStrength = 0;
		}
		
		PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, projectile.ownerId);
		UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(projectile.originModel);
		if (unitStat.getWeaponStatistics() != null) {
			for (WeaponStatistic weaponStat : unitStat.getWeaponStatistics()) {
				if (weaponStat.getId() == projectile.weaponId) {
					weaponStat.setDamageDealt(weaponStat.getDamageDealt() + projectile.damage);
					
					if (target.hull.currentStrength <= 0) {
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
	}

	public Projectile createProjectile(Unit origin, int weaponId, Unit target, GameState state) {
        Projectile projectile = null;
        Weapon weapon = null;
        for (Weapon w : origin.weapons) {
			if (w.id == weaponId) {
				weapon = w;
				break;
			}
		}        
        switch (weapon.data.type) {
		case PROJECTILE:
			projectile = new DirectionalProjectile();
			break;
		case LASER:
			projectile = new DirectionalProjectile();
			break;
		case MISSILE:
			projectile = new HomingProjectile();
			break;
		case BEAM:
			projectile = new DirectionalProjectile();
			break;
		}
        
        super.initializeEntity(projectile, state);
        projectile.ownerId = origin.ownerId;
        projectile.weaponId = weapon.id;
        projectile.originModel = origin.data.model;
        projectile.originId = origin.id;
        projectile.weaponType = weapon.data.type;
		projectile.movementSpeed = weapon.data.speed;
		projectile.damage = weapon.data.damage;
		projectile.aoeRadius = weapon.data.aoeRadius;
		projectile.radius = 1f;
        
		int lifetime = (int)((weapon.data.range / projectile.movementSpeed) * 1000f * 1.5f);
        if (projectile instanceof HomingProjectile) {
        	Vector2 startPosition = VectorUtil.getTargetPosition(origin.position, target.position, origin.radius);
        	projectile.position = startPosition;
			((HomingProjectile) projectile).targetId = target.id;
			projectile.lifetimeMS = lifetime;
			((HomingProjectile) projectile).lastDirection = VectorUtil.getDirection(projectile.position, target.position);
		}
        if (projectile instanceof DirectionalProjectile) {
        	projectile.lifetimeMS = lifetime;
        	projectile.position = VectorUtil.getDirectionalTargetPosition(origin.position, VectorUtil.getDirection(origin.position, target.position), origin.radius);
        	((DirectionalProjectile)projectile).direction = calculateInterceptionDirection(origin, projectile, target, state);
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
    
    private Vector2 calculateInterceptionDirection(Unit origin, Projectile projectile, Unit target, GameState state) {
        Vector2 directAim = VectorUtil.getDirection(projectile.position, target.position);
        
        if (target instanceof Ship) {
            Ship ship = (Ship) target;
            if (!UnitProcessor.isInCombatRange(ship, state) && origin.originPlanetId != ship.targetPlanetId) {
                Planet targetPlanet = EngineUtility.getPlanet(state.entityData.planets, ship.targetPlanetId);
                if (targetPlanet != null) {
                    Vector2 shipDirection = VectorUtil.getDirection(ship.position, targetPlanet.position);
                    Vector2 shipVelocity = new Vector2(shipDirection).scl(((ShipData)ship.data).engine.speed);
                    
                    Vector2 currentShipPosition = new Vector2(ship.position);
                    float projectileSpeed = projectile.movementSpeed;
                    
                    float initialDistance = projectile.position.dst(currentShipPosition);
                    float estimatedTime = initialDistance / projectileSpeed;
                    
                    Vector2 predictedPosition = new Vector2(currentShipPosition).add(
                        new Vector2(shipVelocity).scl(estimatedTime)
                    );
                    
                    for (int i = 0; i < 5; i++) {
                        float distance = projectile.position.dst(predictedPosition);
                        float timeToImpact = distance / projectileSpeed;
                        
                        predictedPosition = new Vector2(currentShipPosition).add(
                            new Vector2(shipVelocity).scl(timeToImpact)
                        );
                    }
                    
                    return new Vector2(predictedPosition).sub(projectile.position).nor();
                }
            }
        }
        return directAim;
    }

    
}
