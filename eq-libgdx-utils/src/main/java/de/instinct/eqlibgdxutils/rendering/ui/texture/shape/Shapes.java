package de.instinct.eqlibgdxutils.rendering.ui.texture.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.EQShape;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQArc;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQCircle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.renderers.ExtendedShapeRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.renderers.GlowRenderer;

public class Shapes {
	
	private static ExtendedShapeRenderer extendedShapeRenderer;
	private static GlowRenderer glowRenderer;
	
	private static Matrix4 defaultMatrix;
	
	public static void init() {
		extendedShapeRenderer = new ExtendedShapeRenderer();
		glowRenderer = new GlowRenderer(extendedShapeRenderer);
		
		defaultMatrix = new Matrix4();
		defaultMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public static void draw(EQShape shape) {
		extendedShapeRenderer.setColor(shape.getColor());
		if (shape.getProjectionMatrix() != null) extendedShapeRenderer.setProjectionMatrix(shape.getProjectionMatrix());
		Gdx.gl.glEnable(GL20.GL_BLEND);
    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		if (shape instanceof EQRectangle) {
			EQRectangle eqRectangle = (EQRectangle) shape;
			if (eqRectangle.getLabel() != null) {
				labeledRectangle(eqRectangle.getBounds(), eqRectangle.getLabel(), shape.getColor());
			} else {
				Rectangle bounds = shape.getProjectionMatrix() == null ? GraphicsUtil.scaleFactorAdjusted(eqRectangle.getBounds()) : eqRectangle.getBounds();
				if (eqRectangle.isRound()) {
					if (eqRectangle.getThickness() == 0) {
						filledRoundRectangle(bounds);
					} else {
						roundRectangle(bounds, eqRectangle.getThickness());
					}
				} else {
					rectangle(bounds, eqRectangle.getThickness());
					if (eqRectangle.getGlowConfig() != null) glowRenderer.rectangle(bounds, eqRectangle.getThickness(), eqRectangle.getGlowConfig());
				}
			}
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
	
	private static void rectangle(Rectangle bounds, float thickness) {
		extendedShapeRenderer.rect(bounds, thickness);
	}
	
	private static void circle(Vector2 position, float radius) {
    	extendedShapeRenderer.circle(position, radius);
	}
	
	private static void cleanArc(Vector2 position, float innerRadius, float outerRadius, float startAngle, float degrees) {
		extendedShapeRenderer.cleanArc(position, innerRadius, outerRadius, startAngle, degrees);
	}
	
	private static void labeledRectangle(Rectangle bounds, String text, Color color) {
		float offset = 20;
		float labelWidth = FontUtil.getFontTextWidthPx(text.length(), FontType.SMALL) + offset;
		
		float adjustedOffset = GraphicsUtil.getHorizontalDisplayScaleFactor() * offset;
		float adjustedLabelWidth = GraphicsUtil.getHorizontalDisplayScaleFactor() * labelWidth;
		Rectangle adjustedBounds = GraphicsUtil.scaleFactorAdjusted(bounds);
		extendedShapeRenderer.partialRect(new Rectangle(adjustedBounds.x, adjustedBounds.y, adjustedBounds.width, adjustedBounds.height - (adjustedOffset / 2)), adjustedLabelWidth, adjustedOffset, 1f);
		extendedShapeRenderer.end();
		
		Label label = new Label(text);
		label.setType(FontType.SMALL);
		label.setBounds(new Rectangle(bounds.x + offset, bounds.y + bounds.height - offset, labelWidth, offset));
		label.setColor(color);
		Border labelBorder = new Border();
		labelBorder.setColor(color);
		labelBorder.setSize(1f);
		label.setBorder(labelBorder);
		label.render();
	}
	
	private static void roundRectangle(Rectangle bounds, float thickness) {
		extendedShapeRenderer.roundRectangle(bounds, thickness);
	}
	
	private static void filledRoundRectangle(Rectangle bounds) {
		extendedShapeRenderer.filledRoundRectangle(bounds);
	}
	
	public static void dispose() {
		extendedShapeRenderer.dispose();
	}

}
