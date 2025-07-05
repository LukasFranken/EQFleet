package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class GraphicsUtil {
	
	public static float getDisplayScaleFactor() {
		return Gdx.graphics.getBackBufferWidth() / (float) Gdx.graphics.getWidth();
	}
	
	public static Rectangle screenBounds() {
		return new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

}
