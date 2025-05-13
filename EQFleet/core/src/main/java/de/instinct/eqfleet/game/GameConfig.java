package de.instinct.eqfleet.game;

import com.badlogic.gdx.graphics.Color;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.Player;

public class GameConfig {
	
	public static Color teammate1Color = Color.BLUE;
	public static Color teammate2Color = Color.PURPLE;
	public static Color teammate3Color = Color.PINK;
	public static Color enemyColor = Color.RED;
	public static Color ancientColor = Color.GOLD;
	public static Color neutralColor = Color.DARK_GRAY;
	
	public static Color getPlayerColor(int playerId) {
		if (playerId == 0) return GameConfig.neutralColor;
		Player player = EngineUtility.getPlayer(Game.activeGameState, playerId);
		Player self = EngineUtility.getPlayer(Game.activeGameState, Game.playerId);
		if (player.teamId != self.teamId) return GameConfig.enemyColor;
		if (playerId == 1 || playerId == 4) return GameConfig.teammate1Color;
		if (playerId == 2 || playerId == 5) return GameConfig.teammate2Color;
		if (playerId == 3 || playerId == 6) return GameConfig.teammate3Color;
		return Color.WHITE;
	}

}
