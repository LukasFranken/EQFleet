package de.instinct.engine.mining.net.message;

import de.instinct.engine.core.order.GameOrder;

public class InputChangedOrder extends GameOrder {
	
	public int playerId;
	public boolean forward;
	public boolean backward;
	public boolean left;
	public boolean right;
	public boolean shoot;

}
