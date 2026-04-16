package de.instinct.engine.fleet.player;

import java.util.List;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.core.player.PlayerProcessor;
import de.instinct.engine.fleet.data.FleetGameState;

public class FleetPlayerProcessor extends PlayerProcessor {
	
	public void initialize(FleetGameState state) {
		for (Player player : state.playerData.players) {
			FleetPlayer fleetPlayer = (FleetPlayer) player;
			fleetPlayer.currentResources = fleetPlayer.startResources;
	    }
	}
	
	public void update(FleetGameState state, long deltaMS) {
		for (Player player : state.playerData.players) {
			FleetPlayer fleetPlayer = (FleetPlayer) player;
	        addResources(fleetPlayer, ((float)deltaMS / 1000f) * fleetPlayer.resourceGenerationSpeed);
	    }
	}
	
	public void addResources(FleetPlayer player, float amount) {
		player.currentResources += amount;
		if (player.currentResources > player.maxResources) {
			player.currentResources = player.maxResources;
		}
	}
	
	public FleetPlayer getFleetPlayer(List<Player> players, int playerId) {
		return (FleetPlayer)super.getPlayer(players, playerId);
	}
	
}
