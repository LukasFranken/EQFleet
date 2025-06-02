package de.instinct.eqfleet.game.frontend.ui;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.ui.model.PlayerData;

public class UIDataUtility {

	public static PlayerData getPlayerData(GameState state) {
		PlayerData playerData = PlayerData.builder().build();
		playerData.setSelf(EngineUtility.getPlayer(state, GameModel.playerId));
		for (Player player : GameModel.activeGameState.players) {
			if (player.playerId == 0) {
				continue;
			}
			if (player.playerId == playerData.getSelf().playerId) {
				continue;
			}
			if (player.teamId == playerData.getSelf().teamId) {
				if (playerData.getTeammate1() == null) {
					playerData.setTeammate1(player);
					continue;
				}
				if (playerData.getTeammate2() == null) {
					playerData.setTeammate2(player);
					continue;
				}
			} else {
				if (playerData.getEnemy1() == null) {
					playerData.setEnemy1(player);
					continue;
				}
				if (playerData.getEnemy2() == null) {
					playerData.setEnemy2(player);
					continue;
				}
				if (playerData.getEnemy3() == null) {
					playerData.setEnemy3(player);
					continue;
				}
			}
		}
		return playerData;
	}
	
}
