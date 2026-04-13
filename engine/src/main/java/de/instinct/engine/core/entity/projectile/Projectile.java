package de.instinct.engine.core.entity.projectile;

import de.instinct.engine.core.entity.Entity;
import lombok.ToString;

@ToString(callSuper = true)
public abstract class Projectile extends Entity {
	
	public int elapsedMS;
	public int lifetimeMS;

	public Projectile clone() {
		Projectile clone = (Projectile) super.clone();
		return clone;
	}
	
}