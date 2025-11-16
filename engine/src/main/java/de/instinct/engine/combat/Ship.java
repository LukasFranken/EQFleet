package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.Unit;
import lombok.ToString;

@ToString(callSuper = true)
public class Ship extends Unit {

	public int targetPlanetId;
	
}
