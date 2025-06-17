package de.instinct.engine.combat.projectile;

import com.badlogic.gdx.math.Vector2;

import lombok.ToString;

@ToString(callSuper = true)
public class HomingProjectile extends Projectile {

	public int targetId;
	public Vector2 lastDirection;
	
	public HomingProjectile clone() {
		HomingProjectile clone = (HomingProjectile) super.clone();
		clone.targetId = this.targetId;
		clone.lastDirection = lastDirection.cpy();
		return clone;
	}

}
