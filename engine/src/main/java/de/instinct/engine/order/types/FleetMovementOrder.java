package de.instinct.engine.order.types;

import de.instinct.engine.order.GameOrder;
import lombok.ToString;

@ToString
public class FleetMovementOrder extends GameOrder {
	
	public int playerId;
	public int fromPlanetId;
	public int toPlanetId;

}
