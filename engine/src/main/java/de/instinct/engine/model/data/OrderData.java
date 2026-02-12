package de.instinct.engine.model.data;

import java.util.List;
import java.util.Queue;

import de.instinct.engine.order.GameOrder;
import lombok.ToString;

@ToString
public class OrderData {
	
	public Queue<GameOrder> unprocessedOrders;
	public List<GameOrder> processedOrders;

}
