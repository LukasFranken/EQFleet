package de.instinct.engine.mining.player;

import java.util.List;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.core.player.PlayerProcessor;
import de.instinct.engine.mining.data.MiningGameState;

public class MiningPlayerProcessor extends PlayerProcessor {

	public void update(MiningGameState miningState, long deltaTime) {
		
	}
	
	public MiningPlayer getMiningPlayer(List<Player> players, int playerId) {
		return (MiningPlayer) getPlayer(players, playerId);
	}

}
