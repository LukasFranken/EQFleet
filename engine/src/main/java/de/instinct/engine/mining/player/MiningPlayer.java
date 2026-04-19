package de.instinct.engine.mining.player;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.mining.entity.ship.data.MiningShipData;
import lombok.ToString;

@ToString(callSuper = true)
public class MiningPlayer extends Player {
	
	public boolean forward;
	public boolean backward;
	public boolean left;
	public boolean right;
	public boolean shoot;
	
	public MiningShipData shipData;
}
