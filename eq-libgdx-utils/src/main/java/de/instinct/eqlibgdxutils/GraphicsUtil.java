package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GraphicsUtil {
	
	private static Vector2 baseWindowSize;
	
	public static void init(Vector2 windowSize) {
		baseWindowSize = windowSize;
	}
	
	public static float getHorizontalDisplayScaleFactor() {
		return (float) Gdx.graphics.getWidth() / (float) baseWindowSize.x;
	}
	
	public static float getVerticalDisplayScaleFactor() {
		return (float) Gdx.graphics.getHeight() / (float) baseWindowSize.y;
	}
	
	public static Rectangle screenBounds() {
		return new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public static Rectangle baseScreenBounds() {
		return new Rectangle(0, 0, baseWindowSize.x, baseWindowSize.y);
	}
	
	public static Rectangle scaleFactorAdjusted(Rectangle unadjustedBounds) {
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		Rectangle adjustedBounds = new Rectangle(unadjustedBounds);
		adjustedBounds.x *= horizontalScaleFactor;
		adjustedBounds.y *= verticalScaleFactor;
		adjustedBounds.width *= horizontalScaleFactor;
		adjustedBounds.height *= verticalScaleFactor;
		return adjustedBounds;
	}
	
	public static Rectangle scaleFactorDeducted(Rectangle unadjustedBounds) {
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		Rectangle adjustedBounds = new Rectangle(unadjustedBounds);
		adjustedBounds.x /= horizontalScaleFactor;
		adjustedBounds.y /= verticalScaleFactor;
		adjustedBounds.width /= horizontalScaleFactor;
		adjustedBounds.height /= verticalScaleFactor;
		return adjustedBounds;
	}

}
