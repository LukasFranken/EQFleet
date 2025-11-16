package de.instinct.engine.model.ship.components;

import de.instinct.engine.model.ship.components.types.ShieldType;
import lombok.ToString;

@ToString
public class ShieldData {
	
	public ShieldType type;
	public float strength;
	public float generation;

}
