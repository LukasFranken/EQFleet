package de.instinct.eqlibgdxutils.rendering.ui.texture.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.EQShape;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQArc;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQCircle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.renderers.ExtendedShapeRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.renderers.GlowRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.renderers.RectangleRenderer;

public class Shapes {
	
	private static ExtendedShapeRenderer extendedShapeRenderer;
	private static GlowRenderer glowRenderer;
	private static RectangleRenderer rectangleRenderer;
	
	private static Matrix4 defaultMatrix;
	
	public static void init() {
		extendedShapeRenderer = new ExtendedShapeRenderer();
		glowRenderer = new GlowRenderer(extendedShapeRenderer);
		rectangleRenderer = new RectangleRenderer(extendedShapeRenderer, glowRenderer);
		
		defaultMatrix = new Matrix4();
		defaultMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public static void draw(EQShape shape) {
		extendedShapeRenderer.setColor(shape.getColor());
		if (shape.getProjectionMatrix() != null) extendedShapeRenderer.setProjectionMatrix(shape.getProjectionMatrix());
		Gdx.gl.glEnable(GL20.GL_BLEND);
    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		if (shape instanceof EQRectangle) {
			rectangleRenderer.render((EQRectangle) shape);
		}
		if (shape instanceof EQCircle) {
			EQCircle eqCircle = (EQCircle) shape;
			Vector2 physicalPosition = shape.getProjectionMatrix() == null ? GraphicsUtil.scaleFactorAdjusted(eqCircle.getPosition()) : eqCircle.getPosition();
			float radius = shape.getProjectionMatrix() == null ? eqCircle.getRadius() * GraphicsUtil.getVerticalDisplayScaleFactor() : eqCircle.getRadius();
			circle(physicalPosition, radius);
		}
		if (shape instanceof EQArc) {
			EQArc eqArc = (EQArc) shape;
			cleanArc(eqArc.getPosition(), eqArc.getInnerRadius(), eqArc.getOuterRadius(), eqArc.getStartAngle(), eqArc.getDegrees());
		}
		Gdx.gl.glDisable(GL20.GL_BLEND);
		extendedShapeRenderer.setProjectionMatrix(defaultMatrix);
	}
	
	private static void circle(Vector2 position, float radius) {
    	extendedShapeRenderer.circle(position, radius);
	}
	
	private static void cleanArc(Vector2 position, float innerRadius, float outerRadius, float startAngle, float degrees) {
		extendedShapeRenderer.cleanArc(position, innerRadius, outerRadius, startAngle, degrees);
	}
	
	public static void dispose() {
		extendedShapeRenderer.dispose();
	}

}
