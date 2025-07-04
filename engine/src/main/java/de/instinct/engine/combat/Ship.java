package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.model.ship.ShipType;
import lombok.ToString;

@ToString
public class Ship extends Unit {

	public String model;
	public ShipType type;
	public float movementSpeed;
	public int cost;
	public int targetPlanetId;
	public int originPlanetId;
	
	public Ship clone() {
		Ship clone = (Ship) super.clone();
		return clone;
	}
	
}
