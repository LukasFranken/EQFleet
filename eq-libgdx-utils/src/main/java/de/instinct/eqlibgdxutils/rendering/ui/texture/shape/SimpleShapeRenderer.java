package de.instinct.eqlibgdxutils.rendering.ui.texture.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;

public class SimpleShapeRenderer {
	
	private static ComplexShapeRenderer shapeRenderer = new ComplexShapeRenderer();
	
	public static void drawRoundRectangle(Rectangle bounds, Color color, float thickness) {
		shapeRenderer.setColor(color);
		shapeRenderer.roundRectangle(bounds, thickness);
	}
	
	public static void drawRectangle(Rectangle bounds, Color color) {
		shapeRenderer.setColor(color);
		Gdx.gl.glEnable(GL20.GL_BLEND);
    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
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
	
	public static void drawLabeledRectangle(Rectangle bounds, String text, Color color) {
		shapeRenderer.setColor(color);
		shapeRenderer.partialRect(new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height - 10), FontUtil.getFontTextWidthPx(text.length(), FontType.SMALL) + 20, 20f, 1f);
		shapeRenderer.end();
		Label label = new Label(text);
		label.setType(FontType.SMALL);
		label.setBounds(new Rectangle(bounds.x + 20, bounds.y + bounds.height - 20, FontUtil.getFontTextWidthPx(text.length(), FontType.SMALL) + 20, 20));
		label.setColor(color);
		Border labelBorder = new Border();
		labelBorder.setColor(color);
		labelBorder.setSize(1f);
		label.setBorder(labelBorder);
		label.render();
	}

}
