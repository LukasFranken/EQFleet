package de.instinct.engine.mining.entity.ship.thruster;

import lombok.ToString;

@ToString
public class MiningThruster {
	
	public float acceleration;
	public float maxSpeed;
	public float deceleration;
	public float maxReverseSpeed;
	public float rotationAcceleration;
	public float rotationSpeed;
	public float maxRotationSpeed;
	public float chargePerSecond;
	public float inertiaDampening;

}
