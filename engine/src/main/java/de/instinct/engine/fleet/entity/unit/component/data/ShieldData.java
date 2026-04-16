package de.instinct.engine.fleet.entity.unit.component.data;

import de.instinct.engine.fleet.entity.unit.component.data.types.ShieldType;
import lombok.ToString;

@ToString(callSuper = true)
public class ShieldData extends ComponentData {
	
	public ShieldType type;
	public float strength;
	public float generation;

}
