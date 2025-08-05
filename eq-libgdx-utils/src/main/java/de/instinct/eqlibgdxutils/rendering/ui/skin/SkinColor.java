package de.instinct.eqlibgdxutils.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;

public enum SkinColor {
	
	RED(new Color(0.7f, 0f, 0f, 1.0f)),
	BLUE(new Color(0f, 0f, 0.7f, 1.0f)),
	PURPLE(new Color(0.4f, 0f, 0.7f, 1.0f)),
	PINK(new Color(0.7f, 0f, 0.4f, 1.0f)),
	ORANGE(new Color(0.7f, 0.4f, 0f, 1.0f)),
	YELLOW(new Color(0.7f, 0.7f, 0f, 1.0f)),
	GRAY(new Color(0.5f, 0.5f, 0.5f, 1.0f)),
	WHITE(new Color(1f, 1f, 1f, 1.0f));
	
	private Color color;
	
	SkinColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}

}
