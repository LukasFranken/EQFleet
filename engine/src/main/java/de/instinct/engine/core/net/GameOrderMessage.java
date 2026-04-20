package de.instinct.engine.core.net;

import de.instinct.engine.core.order.GameOrder;
import lombok.ToString;

@ToString
public class GameOrderMessage extends NetworkMessage {
	
	public GameOrder order;

}
