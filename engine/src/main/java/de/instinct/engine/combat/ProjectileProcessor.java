package de.instinct.engine.combat;

import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.entity.Unit;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine.util.VectorUtil;

public class ProjectileProcessor {

    public void updateProjectiles(GameState state, Combat combat, long deltaTime) {
        Iterator<Projectile> iterator = combat.projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            Unit target = (Unit) EntityManager.getEntity(state, projectile.targetId);
            if (target == null) {
                iterator.remove();
                continue;
            }
            if (updateProjectile(projectile, target, state, deltaTime)) {
                iterator.remove();
            }
        }
    }

    private boolean updateProjectile(Projectile projectile, Unit target, GameState state, long deltaTime) {
        float distanceToTarget = EntityManager.entityDistance(projectile, target);
        float distanceTraveled = projectile.movementSpeed * ((float) deltaTime / 1000f);
        handleMovement(projectile, target, distanceTraveled);
        if (distanceToTarget <= distanceTraveled) {
            handleHit(projectile, target, state);
            return true;
        }
        return false;
    }

    private void handleMovement(Projectile projectile, Unit target, float distanceTraveled) {
        Vector2 targetPosition = VectorUtil.getTargetPosition(projectile.position, target.position, distanceTraveled);
        projectile.position = targetPosition;
    }

    private void handleHit(Projectile projectile, Unit target, GameState state) {
        Player targetOwner = EngineUtility.getPlayer(state.players, target.ownerId);
        Player projectileOwner = EngineUtility.getPlayer(state.players, projectile.ownerId);
        if (targetOwner.teamId != projectileOwner.teamId) {
            dealDamage(projectile, target);
        }
    }

    private void dealDamage(Projectile projectile, Unit target) {
        float remainingDamage = projectile.damage;
        float shieldDamage = Math.min(target.currentShield, remainingDamage);
        target.currentShield -= shieldDamage;
        remainingDamage -= shieldDamage;
        target.currentArmor -= remainingDamage;
    }

    public Projectile createProjectileInstance(Unit origin, Unit target) {
    	Vector2 startPosition = VectorUtil.getTargetPosition(origin.position, target.position, origin.radius);
        Projectile projectile = EntityManager.createProjectile(origin.weapon);
        projectile.ownerId = origin.ownerId;
        projectile.originId = origin.id;
        projectile.targetId = target.id;
        projectile.position = startPosition;
        return projectile;
    }
}
