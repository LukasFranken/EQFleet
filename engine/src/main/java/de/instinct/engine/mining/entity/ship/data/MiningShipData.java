package de.instinct.engine.mining.entity.ship.data;

import lombok.ToString;

@ToString(callSuper = true)
public class MiningShipData {
	
	public float coreCharge;
	
	public float cargoCapacity;
	
	public float acceleration;
	public float deceleration;
	public float maxSpeed;
	public float maxSpeedReverse;
	public float rotationAcceleration;
	public float maxRotationSpeed;
	public float chargePerSecond;
	public float inertiaDampening;
	
	public long cooldownMS;
	public long lifetimeMS;
	public float damage;
	public float projectileSpeed;
	public float chargePerShot;

}
