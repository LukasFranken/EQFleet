package de.instinct.engine.combat.projectile;

import lombok.ToString;

@ToString(callSuper = true)
public class HomingProjectile extends Projectile {

	public int targetId;
	
	public HomingProjectile clone() {
		HomingProjectile clone = (HomingProjectile) super.clone();
		clone.targetId = this.targetId;
		return clone;
	}

}
