package de.instinct.engine.order.types;

import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.OrderType;
import lombok.ToString;

@ToString(callSuper = true)
public class GamePauseOrder extends GameOrder {
	
	@Override
	protected OrderType getOrderType() {
		return OrderType.META;
	}
	
	public int playerId;
	public boolean pause;
	public String reason;
	
}
