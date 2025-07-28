package de.instinct.engine.model.ship;

import lombok.ToString;

@ToString
public class Weapon {
	
	public WeaponType type;
	public float damage;
	public float aoeRadius;
	public float range;
	public float speed;
	public long cooldown;
	public long currentCooldown;
	
	public Weapon clone() {
		Weapon clone = new Weapon();
		clone.type = this.type;
		clone.damage = this.damage;
		clone.aoeRadius = this.aoeRadius;
		clone.range = this.range;
		clone.speed = this.speed;
		clone.cooldown = this.cooldown;
		clone.currentCooldown = this.currentCooldown;
		return clone;
	}

}
