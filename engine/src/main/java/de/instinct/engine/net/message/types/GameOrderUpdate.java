package de.instinct.engine.net.message.types;

import java.util.List;

import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.order.GameOrder;
import lombok.ToString;

@ToString(callSuper = true)
public class GameOrderUpdate extends NetworkMessage {
	
	public List<GameOrder> newOrders;

}
