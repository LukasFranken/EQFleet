package de.instinct.engine.combat.unit.component;

import de.instinct.engine.model.ship.components.ShieldData;
import lombok.ToString;

@ToString
public class Shield {
	
	public int id;
	public double currentStrength;
	public ShieldData data;

}
