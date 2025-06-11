package de.instinct.engine.combat.projectile;

import de.instinct.engine.entity.Entity;
import de.instinct.engine.model.ship.WeaponType;
import lombok.ToString;

@ToString(callSuper = true)
public abstract class Projectile extends Entity {
	
	public int originId;
	public WeaponType weaponType;
	public float movementSpeed;
	public float damage;
	public float elapsedMS;
	public int lifetimeMS;
	public boolean alive;

	public Projectile clone() {
		Projectile clone = (Projectile) super.clone();
		clone.originId = this.originId;
		clone.weaponType = this.weaponType;
		clone.movementSpeed = this.movementSpeed;
		clone.damage = this.damage;
		clone.elapsedMS = this.elapsedMS;
		clone.lifetimeMS = this.lifetimeMS;
		clone.alive = this.alive;
		return clone;
	}
	
}
