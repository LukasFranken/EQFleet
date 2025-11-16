package de.instinct.engine.model.ship.components;

import de.instinct.engine.model.ship.components.types.HullType;
import lombok.ToString;

@ToString
public class HullData {
	
	public HullType type;
	public float strength;
	public float repairSpeed;

}
