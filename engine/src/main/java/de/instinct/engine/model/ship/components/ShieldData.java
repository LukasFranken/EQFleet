package de.instinct.engine.model.ship.components;

import de.instinct.engine.model.ship.components.types.ShieldType;
import lombok.ToString;

@ToString(callSuper = true)
public class ShieldData extends ComponentData {
	
	public ShieldType type;
	public float strength;
	public float generation;

}
