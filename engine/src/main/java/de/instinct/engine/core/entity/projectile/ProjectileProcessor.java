package de.instinct.engine.core.entity.projectile;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.entity.Entity;
import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.core.util.VectorUtil;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.unit.Unit;

public class ProjectileProcessor extends EntityProcessor {
	
	public boolean checkHit(Projectile projectile, Player projectileOwner, Entity potencialTarget, FleetGameState state) {
    	float distanceToTarget = super.entityDistance(projectile, potencialTarget);
    	if (distanceToTarget <= 0) {
    		return true;
    	}
    	return false;
	}
	
	public Vector2 calculateInterceptionDirection(Unit origin, Projectile projectile, Entity target, FleetGameState state) {
        Vector2 direction = VectorUtil.getDirection(projectile.position, target.position);
        Vector2 shipVelocity = new Vector2(direction).scl((float)target.speed);
        
        Vector2 currentShipPosition = new Vector2(target.position);
        double projectileSpeed = projectile.speed;
        
        double initialDistance = projectile.position.dst(currentShipPosition);
        double estimatedTime = initialDistance / projectileSpeed;
        
        Vector2 predictedPosition = new Vector2(currentShipPosition).add(
            new Vector2(shipVelocity).scl((float) estimatedTime)
        );
        
        for (int i = 0; i < 5; i++) {
        	double distance = projectile.position.dst(predictedPosition);
        	double timeToImpact = distance / projectileSpeed;
            
            predictedPosition = new Vector2(currentShipPosition).add(
                new Vector2(shipVelocity).scl((float) timeToImpact)
            );
        }
        
        return new Vector2(predictedPosition).sub(projectile.position).nor();
    }
	
	public void updateLifetime(Projectile projectile, FleetGameState state, long deltaTime) {
    	if (projectile.lifetimeMS < projectile.elapsedMS + deltaTime) {
			deltaTime = projectile.lifetimeMS - projectile.elapsedMS;
			projectile.elapsedMS = projectile.lifetimeMS;
		} else {
			projectile.elapsedMS += deltaTime;
		}
    }

}
