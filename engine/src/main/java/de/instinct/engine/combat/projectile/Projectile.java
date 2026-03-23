package de.instinct.engine.combat.projectile;

import de.instinct.engine.entity.Entity;
import de.instinct.engine.model.ship.components.types.WeaponType;
import lombok.ToString;

@ToString(callSuper = true)
public abstract class Projectile extends Entity {
	
	public int originId;
	public int weaponId;
	public String originModel;
	public WeaponType weaponType;
	public double movementSpeed;
	public double damage;
	public double aoeRadius;
	public int elapsedMS;
	public int lifetimeMS;

	public Projectile clone() {
		Projectile clone = (Projectile) super.clone();
		return clone;
	}
	
}
