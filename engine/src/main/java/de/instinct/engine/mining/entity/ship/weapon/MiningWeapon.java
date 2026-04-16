package de.instinct.engine.mining.entity.ship.weapon;

import lombok.ToString;

@ToString
public class MiningWeapon {
	
	public long cooldownMS;
	public long currentCooldownMS;
	public float damage;
	public float speed;
	public float chargePerShot;
	public long lifetimeMS;

}
