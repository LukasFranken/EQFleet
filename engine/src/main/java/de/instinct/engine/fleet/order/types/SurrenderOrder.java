package de.instinct.engine.fleet.order.types;

import de.instinct.engine.core.order.GameOrder;
import lombok.ToString;

@ToString(callSuper = true)
public class SurrenderOrder extends GameOrder {
	
	public int playerId;

}
