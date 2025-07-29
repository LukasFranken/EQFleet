package de.instinct.engine.order.types;

import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.OrderType;
import lombok.ToString;

@ToString(callSuper = true)
public class SurrenderOrder extends GameOrder {
	
	@Override
	protected OrderType getOrderType() {
		return OrderType.META;
	}
	
	public int playerId;

}
