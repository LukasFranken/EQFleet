package de.instinct.engine.fleet.net.messages;

import java.util.List;

import de.instinct.engine.core.net.NetworkMessage;
import de.instinct.engine.core.order.GameOrder;
import lombok.ToString;

@ToString(callSuper = true)
public class GameOrderUpdate extends NetworkMessage {
	
	public List<GameOrder> newOrders;

}
