package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.model.ship.ShipType;
import lombok.ToString;

@ToString(callSuper = true)
public class Ship extends Unit {

	public ShipType type;
	public float movementSpeed;
	public int targetPlanetId;
	
	public Ship clone() {
		Ship clone = (Ship) super.clone();
		return clone;
	}
	
}
