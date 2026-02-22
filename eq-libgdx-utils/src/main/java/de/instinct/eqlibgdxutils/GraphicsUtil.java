package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class GraphicsUtil {
	
	private static String LOGTAG = "GRAPHICS";
	private static Vector2 baseWindowSize;
	
	private static Rectangle physicalBounds;
	private static Rectangle screenBounds;
	
	public static void init(Vector2 windowSize) {
		baseWindowSize = windowSize;
		physicalBounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenBounds = new Rectangle(0, 0, baseWindowSize.x, baseWindowSize.y);
    	Logger.log(LOGTAG, "Running on display size: " + physicalBounds(), ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Base window size: " + baseWindowSize, ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Display scale factor: " + getScaleFactor(), ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Horizontal display scale factor: " + getHorizontalDisplayScaleFactor(), ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Vertical display scale factor: " + getVerticalDisplayScaleFactor(), ConsoleColor.YELLOW);
	}
	
	public static float getHorizontalDisplayScaleFactor() {
		return (float) Gdx.graphics.getWidth() / (float) baseWindowSize.x;
	}
	
	public static float getVerticalDisplayScaleFactor() {
		return (float) Gdx.graphics.getHeight() / (float) baseWindowSize.y;
	}
	
	public static Rectangle physicalBounds() {
		return physicalBounds;
	}
	
	public static Rectangle screenBounds() {
		return screenBounds;
	}
	
	public static Rectangle scaleFactorAdjusted(Rectangle bounds) {
		if (bounds == null) return null;
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		bounds.x *= horizontalScaleFactor;
		bounds.y *= verticalScaleFactor;
		bounds.width *= horizontalScaleFactor;
		bounds.height *= verticalScaleFactor;
		return bounds;
	}
	
	public static Vector2 scaleFactorAdjusted(Vector2 position) {
		if (position == null) return null;
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		position.x *= horizontalScaleFactor;
		position.y *= verticalScaleFactor;
		return position;
	}
	
	public static Rectangle scaleFactorDeducted(Rectangle bounds) {
		if (bounds == null) return null;
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		bounds.x /= horizontalScaleFactor;
		bounds.y /= verticalScaleFactor;
		bounds.width /= horizontalScaleFactor;
		bounds.height /= verticalScaleFactor;
		return bounds;
	}
	
	public static Vector2 scaleFactorDeducted(Vector2 position) {
		if (position == null) return null;
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		position.x /= horizontalScaleFactor;
		position.y /= verticalScaleFactor;
		return position;
	}

	public static float getScaleFactor() {
		return (float) Math.sqrt(getHorizontalDisplayScaleFactor() * getVerticalDisplayScaleFactor());
	}

}
