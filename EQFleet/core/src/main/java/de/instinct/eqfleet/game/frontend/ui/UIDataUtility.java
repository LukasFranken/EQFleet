package de.instinct.eqfleet.game.frontend.ui;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.player.FleetPlayer;
import de.instinct.engine_api.core.service.EngineDataInterface;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.ui.model.PlayerData;

public class UIDataUtility {

	public static PlayerData getPlayerData(FleetGameState state) {
		PlayerData playerData = PlayerData.builder().build();
		playerData.setSelf(EngineDataInterface.getPlayer(state.playerData.players, GameModel.playerId));
		for (Player player : GameModel.activeGameState.playerData.players) {
			if (player.id == 0) {
				continue;
			}
			if (player.id == playerData.getSelf().id) {
				continue;
			}
			if (player.teamId == playerData.getSelf().teamId) {
				if (playerData.getTeammate1() == null) {
					playerData.setTeammate1((FleetPlayer) player);
					continue;
				}
				if (playerData.getTeammate2() == null) {
					playerData.setTeammate2((FleetPlayer) player);
					continue;
				}
			} else {
				if (playerData.getEnemy1() == null) {
					playerData.setEnemy1((FleetPlayer) player);
					continue;
				}
				if (playerData.getEnemy2() == null) {
					playerData.setEnemy2((FleetPlayer) player);
					continue;
				}
				if (playerData.getEnemy3() == null) {
					playerData.setEnemy3((FleetPlayer) player);
					continue;
				}
			}
		}
		return playerData;
	}
	
	public static double calculateTotalResourceGenerationSpeed(FleetGameState state, FleetPlayer player) {
		double totalResourceGenerationSpeed = player.resourceGenerationSpeed;
		for (Planet planet : state.entityData.planets) {
			if (planet.ownerId == player.id) {
				if (planet.ancient) {
					totalResourceGenerationSpeed -= state.staticData.ancientPlanetResourceDegradationFactor;
				} else {
					totalResourceGenerationSpeed += planet.resourceGenerationSpeed;
				}
			}
		}
		return totalResourceGenerationSpeed;
	}
	
}
