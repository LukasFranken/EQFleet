package de.instinct.devtools.util;

import java.awt.Color;

public class EngineUtils {
	
	public static final Color NEUTRAL_COLOR = Color.GRAY;
	public static Color ANCIENT_COLOR = Color.ORANGE;
	public static Color PLAYER_COLOR = Color.BLUE;
	public static Color PLAYER2_COLOR = Color.PINK;
	public static Color PLAYER3_COLOR = Color.MAGENTA;
	public static Color ENEMY_COLOR = Color.RED;
	
	public static Color getOwnerColor(int ownerId) {
		if (ownerId == 0) return NEUTRAL_COLOR;
		if (ownerId == 1) return PLAYER_COLOR;
		if (ownerId == 2) return PLAYER2_COLOR;
		if (ownerId == 3) return PLAYER3_COLOR;
		return ENEMY_COLOR;
	}

	public static Color getPlanetColor(int ownerId, boolean ancient) {
		return ancient ? ANCIENT_COLOR : getOwnerColor(ownerId);
	}

}
