package de.instinct.eqlibgdxutils.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;

public enum SkinColor {
	
	RED(new Color(0.7f, 0f, 0f, 1.0f)),
	BLUE(new Color(0f, 0f, 0.7f, 1.0f)),
	GREEN(new Color(0f, 0.7f, 0f, 1.0f)),
	GRAY(new Color(0.7f, 0.7f, 0.7f, 1.0f));
	
	private Color color;
	
	SkinColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}

}
