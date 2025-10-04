package de.instinct.engine.combat.projectile;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.UnitProcessor;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.stats.model.unit.UnitStatistic;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine.util.VectorUtil;

public class ProjectileProcessor extends EntityProcessor {

    public void updateProjectiles(GameState state, long deltaTime) {
        for (Projectile projectile : state.projectiles) {
        	updateProjectile(projectile, state, deltaTime);
        }
        super.removeDestroyed(state.projectiles.iterator());
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
    	float remainingMissileRange = (projectile.lifetimeMS - projectile.elapsedMS) / 1000f * projectile.movementSpeed;
		return UnitProcessor.getClosestInRangeTarget(projectile, remainingMissileRange, state);
	}

	private void calculateHit(Projectile projectile, GameState state) {
    	Player projectileOwner = EngineUtility.getPlayer(state.players, projectile.ownerId);
        for (Ship ship : state.ships) {
        	if (checkHit(projectile, projectileOwner, ship, state)) {
        		calculateDamage(projectile, ship, state);
        		projectile.flaggedForDestroy = true;
        		break;
        	}
        }
        if (!projectile.flaggedForDestroy) {
        	for (Turret turret : state.turrets) {
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
    		Player targetOwner = EngineUtility.getPlayer(state.players, potencialTarget.ownerId);
        	if (projectileOwner.teamId != targetOwner.teamId && potencialTarget.defense != null) {
        		return true;
        	}
    	}
    	return false;
	}

	private void calculateDamage(Projectile projectile, Unit target, GameState state) {
        if (projectile.aoeRadius > 0) {
        	Player projectileOwner = EngineUtility.getPlayer(state.players, projectile.ownerId);
        	for (Ship pentencialTargetShip : state.ships) {
        		float distanceToTarget = EntityManager.entityDistance(projectile, pentencialTargetShip);
        		if (distanceToTarget < projectile.aoeRadius) {
        			if (projectileOwner.teamId != EngineUtility.getPlayer(state.players, pentencialTargetShip.ownerId).teamId) {
        				if (pentencialTargetShip.defense != null) {
        					dealDamage(projectile, pentencialTargetShip, state);
        				}
        			}
        		}
        	}
        	for (Turret pentencialTargetTurret : state.turrets) {
        		float distanceToTarget = EntityManager.entityDistance(projectile, pentencialTargetTurret);
        		if (distanceToTarget < projectile.aoeRadius) {
        			if (projectileOwner.teamId != EngineUtility.getPlayer(state.players, pentencialTargetTurret.ownerId).teamId) {
        				if (pentencialTargetTurret.defense != null) {
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
		float shieldDamage = Math.min(target.defense.currentShield, remainingDamage);
		target.defense.currentShield -= shieldDamage;
		remainingDamage -= shieldDamage;
		target.defense.currentArmor -= remainingDamage;
		
		PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, projectile.ownerId);
		UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(projectile.originModel);
		unitStat.setDamageDealt(unitStat.getDamageDealt() + projectile.damage);
		if (target.defense.currentArmor <= 0) {
			unitStat.setKills(unitStat.getKills() + 1);
		}
		
		PlayerStatistic targetUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, target.ownerId);
		UnitStatistic targetUnitStat = targetUnitOwnerStatistic.getUnit(target.model);
		targetUnitStat.setDamageTaken(unitStat.getDamageTaken() + projectile.damage);
	}

	public Projectile createProjectile(Unit origin, Unit target, GameState state) {
        Projectile projectile = null;
        switch (origin.weapon.type) {
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
        projectile.originModel = origin.model;
        projectile.originId = origin.id;
        projectile.weaponType = origin.weapon.type;
		projectile.movementSpeed = origin.weapon.speed;
		projectile.damage = origin.weapon.damage;
		projectile.aoeRadius = origin.weapon.aoeRadius;
		projectile.radius = 1f;
        
        if (projectile instanceof HomingProjectile) {
        	Vector2 startPosition = VectorUtil.getTargetPosition(origin.position, target.position, origin.radius);
        	projectile.position = startPosition;
			((HomingProjectile) projectile).targetId = target.id;
			projectile.lifetimeMS = 3000;
			((HomingProjectile) projectile).lastDirection = VectorUtil.getDirection(projectile.position, target.position);
		}
        if (projectile instanceof DirectionalProjectile) {
        	projectile.lifetimeMS = (int)(origin.weapon.range / projectile.movementSpeed * 1000);
        	projectile.position = VectorUtil.getDirectionalTargetPosition(origin.position, VectorUtil.getDirection(origin.position, target.position), origin.radius);
        	((DirectionalProjectile)projectile).direction = calculateInterceptionDirection(projectile, target, state);
        }
        return projectile;
    }
    
    private Vector2 calculateInterceptionDirection(Projectile projectile, Unit target, GameState state) {
        Vector2 directAim = VectorUtil.getDirection(projectile.position, target.position);
        
        if (target instanceof Ship) {
            Ship ship = (Ship) target;
            Unit closestInRangeTarget = UnitProcessor.getClosestInRangeTarget(ship, ship.weapon.range, state);
            if (closestInRangeTarget == null && ship.targetPlanetId > 0) {
                Planet targetPlanet = EngineUtility.getPlanet(state.planets, ship.targetPlanetId);
                if (targetPlanet != null) {
                    Vector2 shipDirection = VectorUtil.getDirection(ship.position, targetPlanet.position);
                    Vector2 shipVelocity = new Vector2(shipDirection).scl(ship.movementSpeed);
                    
                    Vector2 currentShipPosition = new Vector2(ship.position);
                    float projectileSpeed = projectile.movementSpeed;
                    
                    float initialDistance = projectile.position.dst(currentShipPosition);
                    float estimatedTime = initialDistance / projectileSpeed;
                    
                    Vector2 predictedPosition = new Vector2(currentShipPosition).add(
                        new Vector2(shipVelocity).scl(estimatedTime)
                    );
                    
                    for (int i = 0; i < 3; i++) {
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
