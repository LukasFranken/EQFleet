package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.Unit;
import lombok.ToString;

@ToString(callSuper = true)
public class Turret extends Unit {
	
	public String model;
	public float rotationSpeed;
	
	public Turret clone() {
		Turret clone = (Turret) super.clone();
		return clone;
	}

}
