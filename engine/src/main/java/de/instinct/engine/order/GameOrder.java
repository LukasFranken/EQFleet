package de.instinct.engine.order;

import lombok.ToString;

@ToString
public abstract class GameOrder {
	
	public GameOrder() {
		this.type = getOrderType();
	}
	
	protected abstract OrderType getOrderType();

	public long orderId;
	public OrderType type;
	public long acceptedTimeMS;
	public boolean processed;

}
