package de.instinct.engine.order.types;

import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.OrderType;
import lombok.ToString;

@ToString
public class ShipMovementOrder extends GameOrder {
	
	@Override
	protected OrderType getOrderType() {
		return OrderType.COMBAT;
	}
	
	public int playerId;
	public int playerShipId;
	public int fromPlanetId;
	public int toPlanetId;

}
