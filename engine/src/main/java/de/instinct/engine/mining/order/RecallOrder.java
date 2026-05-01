package de.instinct.engine.mining.order;

import de.instinct.engine.core.order.GameOrder;
import lombok.ToString;

@ToString(callSuper = true)
public class RecallOrder extends GameOrder {
	
	public int playerId;

}
