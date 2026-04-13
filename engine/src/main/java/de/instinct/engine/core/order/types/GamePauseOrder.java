package de.instinct.engine.core.order.types;

import de.instinct.engine.core.order.GameOrder;
import lombok.ToString;

@ToString(callSuper = true)
public class GamePauseOrder extends GameOrder {
	
	public int playerId;
	public boolean pause;
	public String reason;
	
}
