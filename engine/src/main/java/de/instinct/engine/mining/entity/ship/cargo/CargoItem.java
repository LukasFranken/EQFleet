package de.instinct.engine.mining.entity.ship.cargo;

import de.instinct.engine.mining.entity.asteroid.ResourceType;
import lombok.ToString;

@ToString
public class CargoItem {
	
	public ResourceType resourceType;
	public float amount;

}
