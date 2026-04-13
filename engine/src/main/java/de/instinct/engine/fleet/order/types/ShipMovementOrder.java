package de.instinct.engine.fleet.order.types;

import de.instinct.engine.core.order.GameOrder;
import lombok.ToString;

@ToString(callSuper = true)
public class ShipMovementOrder extends GameOrder {
	
	public int playerId;
	public int playerShipId;
	public int fromPlanetId;
	public int toPlanetId;

}
