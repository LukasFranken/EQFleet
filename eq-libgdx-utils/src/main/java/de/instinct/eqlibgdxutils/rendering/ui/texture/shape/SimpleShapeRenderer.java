package de.instinct.eqlibgdxutils.rendering.ui.texture.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SimpleShapeRenderer {
	
	private static ComplexShapeRenderer shapeRenderer = new ComplexShapeRenderer();
	
	public static void drawRoundRectangle(Rectangle rectangle, Color color, float thickness) {
		shapeRenderer.setColor(color);
		shapeRenderer.roundRectangle(rectangle, thickness);
	}
	
	public static void drawRectangle(Rectangle rectangle, Color color) {
		shapeRenderer.setColor(color);
		Gdx.gl.glEnable(GL20.GL_BLEND);
    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		shapeRenderer.end();
	}
	
	public static void drawCircle(Vector2 position, float radius, Color color) {
		shapeRenderer.setColor(color);
		Gdx.gl.glEnable(GL20.GL_BLEND);
    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.circle(position.x, position.y, radius);
		shapeRenderer.end();
	}

}
