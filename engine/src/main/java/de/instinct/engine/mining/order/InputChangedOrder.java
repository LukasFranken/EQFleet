package de.instinct.engine.mining.order;

import de.instinct.engine.core.order.GameOrder;
import lombok.ToString;

@ToString(callSuper = true)
public class InputChangedOrder extends GameOrder {
	
	public int playerId;
	public boolean forward;
	public boolean backward;
	public boolean left;
	public boolean right;
	public boolean shoot;

}
