package de.instinct.engine.player;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;

public class PlayerProcessor {
	
	public static void initialize(GameState state) {
		for (Player player : state.staticData.playerData.players) {
	        player.currentResources = player.startResources;
	    }
	}
	
	public static void update(GameState state, long deltaMS) {
		for (Player player : state.staticData.playerData.players) {
	        addResources(player, ((double)deltaMS / 1000D) * player.resourceGenerationSpeed);
	    }
	}
	
	public static void addResources(Player player, double amount) {
		player.currentResources += amount;
		if (player.currentResources > player.maxResources) {
			player.currentResources = player.maxResources;
		}
	}
	
}
