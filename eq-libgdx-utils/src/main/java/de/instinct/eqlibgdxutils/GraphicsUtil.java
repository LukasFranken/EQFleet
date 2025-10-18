package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class GraphicsUtil {
	
	private static String LOGTAG = "GRAPHICS";
	private static Vector2 baseWindowSize;
	
	public static void init(Vector2 windowSize) {
		baseWindowSize = windowSize;
    	Logger.log(LOGTAG, "Running on display size: " + physicalBounds(), ConsoleColor.YELLOW);
    	Logger.log(LOGTAG, "Base window size: " + baseWindowSize, ConsoleColor.YELLOW);
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
		return new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public static Rectangle screenBounds() {
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
	
	public static Vector2 scaleFactorAdjusted(Vector2 unadjustedPosition) {
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		Vector2 adjustedPosition = new Vector2(unadjustedPosition);
		adjustedPosition.x *= horizontalScaleFactor;
		adjustedPosition.y *= verticalScaleFactor;
		return adjustedPosition;
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
	
	public static Vector2 scaleFactorDeducted(Vector2 unadjustedPosition) {
		float horizontalScaleFactor = getHorizontalDisplayScaleFactor();
		float verticalScaleFactor = getVerticalDisplayScaleFactor();
		Vector2 adjustedPosition = new Vector2(unadjustedPosition);
		adjustedPosition.x /= horizontalScaleFactor;
		adjustedPosition.y /= verticalScaleFactor;
		return adjustedPosition;
	}

}
