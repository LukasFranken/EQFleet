package de.instinct.engine.core.data;

import de.instinct.engine.core.meta.data.MetaData;
import de.instinct.engine.core.order.data.OrderData;
import de.instinct.engine.core.player.data.PlayerData;
import lombok.ToString;

@ToString(callSuper = true)
public abstract class GameState {
	
	public MetaData metaData;
	public OrderData orderData;
	public PlayerData playerData;

}
