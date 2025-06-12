package de.instinct.eqlibgdxutils.debug.logging;

import com.badlogic.gdx.graphics.Color;

public enum ConsoleColor {
	
	DEFAULT("\u001B[0m"),
	BLACK("\u001B[30m"),
	RED("\u001B[31m"),
	GREEN("\u001B[32m"),
	YELLOW("\u001B[33m"),
	BLUE("\u001B[34m"),
	MAGENTA("\u001B[35m"),
	CYAN("\u001B[36m"),
	PINK("\u001B[38;2;255;105;180m"),
	GRAY("\u001b[38;2;150;150;150m");
	
	private String code;
	
	ConsoleColor(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}

	public Color getGameColor() {
		switch (this) {
			case BLACK:
				return Color.BLACK;
			case RED:
				return Color.RED;
			case GREEN:
				return Color.GREEN;
			case YELLOW:
				return Color.YELLOW;
			case BLUE:
				return Color.BLUE;
			case MAGENTA:
				return Color.MAGENTA;
			case CYAN:
				return Color.CYAN;
			case PINK:
				return new Color(1f, 0.41f, 0.71f, 1f);
			case GRAY:
				return new Color(0.588f, 0.588f, 0.588f, 1f);
			default:
				return Color.WHITE;
		}
	}

}
