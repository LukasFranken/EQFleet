package de.instinct.engine.fleet.entity.unit.ship;

import de.instinct.engine.fleet.entity.unit.Unit;
import lombok.ToString;

@ToString(callSuper = true)
public class Ship extends Unit {

	public int targetPlanetId;
	
}
