package de.instinct.engine.combat;

import de.instinct.engine.entity.Entity;
import de.instinct.engine.model.ship.WeaponType;
import lombok.ToString;

@ToString
public class Projectile extends Entity {
	
	public int targetId;
	public WeaponType weaponType;
	public float movementSpeed;
	public float damage;

	public Projectile clone() {
		Projectile clone = (Projectile) super.clone();
		return clone;
	}
	
}
