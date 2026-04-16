package de.instinct.engine.fleet.entity.unit.component;

import de.instinct.engine.fleet.entity.unit.component.data.ShieldData;
import lombok.ToString;

@ToString
public class Shield {
	
	public int id;
	public float currentStrength;
	public ShieldData data;

}
