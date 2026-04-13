package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class GraphicsUtil {
	
	private static String LOGTAG = "GRAPHICS";
	
	private static Rectangle physicalBounds;
	private static Rectangle screenBounds;
	
	public static void init(Vector2 windowSize) {
		physicalBounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenBounds = new Rectangle(0, 0, windowSize.x, windowSize.y);
    	Logger.log(LOGTAG, "Running on display size: " + physicalBounds(), ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Base window size: " + screenBounds, ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Display scale factor: " + getScaleFactor(), ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Horizontal display scale factor: " + getHorizontalDisplayScaleFactor(), ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Vertical display scale factor: " + getVerticalDisplayScaleFactor(), ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Pixel density: " + Gdx.graphics.getDensity(), ConsoleColor.YELLOW);
	}
	
	public static float getHorizontalDisplayScaleFactor() {
		return (float) Gdx.graphics.getWidth() / (float) screenBounds.width;
	}
	
	public static float getVerticalDisplayScaleFactor() {
		return (float) Gdx.graphics.getHeight() / (float) screenBounds.height;
	}
	
	public static Rectangle physicalBounds() {
		return physicalBounds;
	}
	
	public static Rectangle screenBounds() {
		return screenBounds;
	}
	
	public static Rectangle translateToPhysical(Rectangle bounds) {
		if (bounds == null) return null;
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		bounds.x *= horizontalScaleFactor;
		bounds.y *= verticalScaleFactor;
		bounds.width *= horizontalScaleFactor;
		bounds.height *= verticalScaleFactor;
		return bounds;
	}
	
	public static Vector2 translateToPhysical(Vector2 position) {
		if (position == null) return null;
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		position.x *= horizontalScaleFactor;
		position.y *= verticalScaleFactor;
		return position;
	}
	
	public static Rectangle translateToVirtual(Rectangle bounds) {
		if (bounds == null) return null;
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		bounds.x /= horizontalScaleFactor;
		bounds.y /= verticalScaleFactor;
		bounds.width /= horizontalScaleFactor;
		bounds.height /= verticalScaleFactor;
		return bounds;
	}
	
	public static Vector2 translateToVirtual(Vector2 position) {
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
