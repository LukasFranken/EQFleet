package de.instinct.engine.combat.unit.component;

import de.instinct.engine.model.ship.components.ShieldData;
import lombok.ToString;

@ToString
public class Shield {
	
	public int id;
	public float currentStrength;
	public ShieldData data;

}
