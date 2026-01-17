package de.instinct.engine.model.ship.components;

import de.instinct.engine.model.ship.components.types.HullType;
import lombok.ToString;

@ToString(callSuper = true)
public class HullData extends ComponentData {
	
	public HullType type;
	public float strength;
	public float repairSpeed;

}
