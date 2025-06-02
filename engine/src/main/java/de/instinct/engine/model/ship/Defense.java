package de.instinct.engine.model.ship;

import lombok.ToString;

@ToString
public class Defense {

	public float shieldRegenerationSpeed;
	public float shield;
	public float armor;
	
	public Defense clone() {
		Defense clone = new Defense();
		clone.shieldRegenerationSpeed = this.shieldRegenerationSpeed;
		clone.shield = this.shield;
		clone.armor = this.armor;
		return clone;
	}
	
}
