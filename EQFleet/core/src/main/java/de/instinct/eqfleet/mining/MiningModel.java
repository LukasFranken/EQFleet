package de.instinct.eqfleet.mining;

import java.util.Queue;

import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.data.MiningGameState;

public class MiningModel {
	
	public static volatile MiningMode mode;
	
	public static volatile int playerId;
	public static volatile MiningGameState state;
	public static volatile Queue<GameOrder> inputOrders;

}
