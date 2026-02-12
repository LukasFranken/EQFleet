package de.instinct.engine.order;

import lombok.ToString;

@ToString
public abstract class GameOrder {
	
	public GameOrder() {
		this.type = getOrderType();
	}
	
	protected abstract OrderType getOrderType();

	public long processGameTimeStamp;
	public OrderType type;
	
	public GameOrder clone() {
		try {
			return (GameOrder) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cloning not supported for GameOrder", e);
		}
	}

}
