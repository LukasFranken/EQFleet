package de.instinct.engine.mining.entity.ship;

import de.instinct.engine.core.entity.Entity;

public class PlayerShip extends Entity {
	
	public float acceleration;
	public float maxSpeed;
	public float deceleration;
	public float maxReverseSpeed;
	public float rotationSpeed;
	public float laserSpeed;
	public float laserDamage;
	public long laserCooldownMS;
	public long laserLifetimeMS;

}
