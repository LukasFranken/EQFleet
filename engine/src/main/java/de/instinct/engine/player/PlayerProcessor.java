package de.instinct.engine.player;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;

public class PlayerProcessor {
	
	public void update(GameState state, long deltaMS) {
		for (Player player : state.staticData.playerData.players) {
	        player.currentCommandPoints += ((double) deltaMS / 1000D) * player.commandPointsGenerationSpeed;
	        if (player.currentCommandPoints > player.maxCommandPoints) {
	            player.currentCommandPoints = player.maxCommandPoints;
	        }
	    }
	}

}
