package de.instinct.engine.core.player;

import java.util.List;

public class PlayerProcessor {
	
	public Player getPlayer(List<Player> players, int playerId) {
		for (Player player : players) {
			if (player.id == playerId) {
				return player;
			}
		}
		throw new IllegalStateException("Player with playerId " + playerId + " not found in List of size: " + players.size());
	}

}
