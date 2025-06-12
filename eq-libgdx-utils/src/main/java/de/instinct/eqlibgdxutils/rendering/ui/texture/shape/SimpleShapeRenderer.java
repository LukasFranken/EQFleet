package de.instinct.eqlibgdxutils.rendering.ui.texture.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class SimpleShapeRenderer {
	
	private static ComplexShapeRenderer shapeRenderer = new ComplexShapeRenderer();
	
	public static void drawRectangle(Rectangle rectangle, Color color, float thickness) {
		shapeRenderer.setColor(color);
		shapeRenderer.roundRectangle(rectangle, thickness);
	}

}
