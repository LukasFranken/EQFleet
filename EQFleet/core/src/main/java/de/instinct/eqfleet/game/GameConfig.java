package de.instinct.eqfleet.game;

import com.badlogic.gdx.graphics.Color;

import de.instinct.engine.core.player.Player;
import de.instinct.engine_api.core.service.EngineDataInterface;

public class GameConfig {
	
	public static Color teammate1Color = Color.BLUE;
	public static Color teammate2Color = Color.CYAN;
	public static Color teammate3Color = Color.GREEN;
	public static Color enemy1Color = Color.RED;
	public static Color enemy2Color = Color.PURPLE;
	public static Color enemy3Color = Color.PINK;
	public static Color ancientColor = Color.GOLD;
	public static Color neutralColor = Color.DARK_GRAY;
	
	public static Color getPlayerColor(int playerId) {
		if (playerId == 0) return GameConfig.neutralColor;
		Player player = EngineDataInterface.getPlayer(GameModel.activeGameState.playerData.players, playerId);
		Player self = EngineDataInterface.getPlayer(GameModel.activeGameState.playerData.players, GameModel.playerId);
		int playerCount = GameModel.activeGameState.playerData.players.size();
		boolean ownTeam = player.teamId == self.teamId;
		return getPlayerColor(playerId, playerCount, ownTeam);
	}
	
	public static Color getPlayerColor(int playerId, int playerCount, boolean ownTeam) {
		if (playerId == 0) return GameConfig.neutralColor;
		if (playerCount == 3) {
			if (ownTeam) {
				return GameConfig.teammate1Color;
			} else {
				return GameConfig.enemy1Color;
			}
		}
		if (playerCount == 5) {
			if (ownTeam) {
				if (playerId == 1 || playerId == 3) return GameConfig.teammate1Color;
				if (playerId == 2 || playerId == 4) return GameConfig.teammate2Color;
			} else {
				if (playerId == 1 || playerId == 3) return GameConfig.enemy1Color;
				if (playerId == 2 || playerId == 4) return GameConfig.enemy2Color;
			}
		}
		if (playerCount == 7) {
			if (ownTeam) {
				if (playerId == 1 || playerId == 4) return GameConfig.teammate1Color;
				if (playerId == 2 || playerId == 5) return GameConfig.teammate2Color;
				if (playerId == 3 || playerId == 6) return GameConfig.teammate3Color;
			} else {
				if (playerId == 1 || playerId == 4) return GameConfig.enemy1Color;
				if (playerId == 2 || playerId == 5) return GameConfig.enemy2Color;
				if (playerId == 3 || playerId == 6) return GameConfig.enemy3Color;
			}
		}
		return Color.WHITE;
	}

}
