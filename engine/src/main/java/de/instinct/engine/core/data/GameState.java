package de.instinct.engine.core.data;

import de.instinct.engine.core.meta.data.MetaData;
import de.instinct.engine.core.player.data.PlayerData;
import de.instinct.engine.fleet.order.data.OrderData;

public abstract class GameState {
	
	public String gameUUID;
	public long gameTimeMS;
	public boolean started;
	public MetaData metaData;
	public OrderData orderData;
	public PlayerData playerData;

}
