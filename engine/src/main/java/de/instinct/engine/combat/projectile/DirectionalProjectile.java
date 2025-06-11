package de.instinct.engine.combat.projectile;

import com.badlogic.gdx.math.Vector2;

import lombok.ToString;

@ToString(callSuper = true)
public class DirectionalProjectile extends Projectile {
	
	public Vector2 direction;
	
	public DirectionalProjectile clone() {
		DirectionalProjectile clone = (DirectionalProjectile) super.clone();
		clone.direction = this.direction.cpy();
		return clone;
	}

}
