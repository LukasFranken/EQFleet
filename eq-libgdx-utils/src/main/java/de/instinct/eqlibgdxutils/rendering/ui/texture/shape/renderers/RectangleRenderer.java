package de.instinct.eqlibgdxutils.rendering.ui.texture.shape.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class RectangleRenderer {
	
	private ExtendedShapeRenderer extendedShapeRenderer;
	private GlowRenderer glowRenderer;
	
	private Label labeledRectangleLabel;
	
	
	public RectangleRenderer(ExtendedShapeRenderer extendedShapeRenderer, GlowRenderer glowRenderer) {
		this.extendedShapeRenderer = extendedShapeRenderer;
		this.glowRenderer = glowRenderer;
		labeledRectangleLabel = new Label("");
		labeledRectangleLabel.setType(FontType.SMALL);
		labeledRectangleLabel.setBounds(new Rectangle());
		labeledRectangleLabel.setColor(new Color());
		Border labelBorder = new Border();
		labelBorder.setColor(new Color());
		labelBorder.setSize(1f);
		labeledRectangleLabel.setBorder(labelBorder);
	}
	
	public void render(EQRectangle rectangle) {
		if (rectangle.getLabel() != null) {
			labeledRectangle(rectangle.getBounds(), rectangle.getLabel(), rectangle.getColor());
		} else {
			Rectangle bounds = rectangle.getProjectionMatrix() == null ? GraphicsUtil.scaleFactorAdjusted(rectangle.getBounds()) : rectangle.getBounds();
			float thickness = rectangle.getProjectionMatrix() == null ? GraphicsUtil.getScaleFactor() * rectangle.getThickness() : rectangle.getThickness();
			if (rectangle.isRound()) {
				if (rectangle.getThickness() == 0) {
					filledRoundRectangle(bounds);
				} else {
					roundRectangle(bounds, thickness);
				}
			} else {
				if (rectangle.isFilled()) {
					rectangle(bounds);
				} else {
					rectangle(bounds, thickness);
				}
				if (rectangle.getGlowConfig() != null) glowRenderer.rectangle(bounds, rectangle.getThickness(), rectangle.getGlowConfig());
			}
		}
	}
	
	private void rectangle(Rectangle bounds) {
		extendedShapeRenderer.rect(bounds);
	}
	
	private void rectangle(Rectangle bounds, float thickness) {
		extendedShapeRenderer.rect(bounds, thickness);
	}
	
	private void labeledRectangle(Rectangle bounds, String text, Color color) {
		float offset = 20;
		float labelWidth = FontUtil.getFontTextWidthPx(text.length(), FontType.SMALL) + offset;
		
		float adjustedOffset = GraphicsUtil.getHorizontalDisplayScaleFactor() * offset;
		float adjustedLabelWidth = GraphicsUtil.getHorizontalDisplayScaleFactor() * labelWidth;
		Rectangle adjustedBounds = GraphicsUtil.scaleFactorAdjusted(bounds);
		extendedShapeRenderer.partialRect(new Rectangle(adjustedBounds.x, adjustedBounds.y, adjustedBounds.width, adjustedBounds.height - (adjustedOffset / 2)), adjustedLabelWidth, adjustedOffset, 1f);
		extendedShapeRenderer.end();
		
		labeledRectangleLabel.setText(text);
		labeledRectangleLabel.getBounds().set(bounds.x + offset, bounds.y + bounds.height - offset, labelWidth, offset);
		labeledRectangleLabel.setColor(color);
		labeledRectangleLabel.getBorder().setColor(color);
		labeledRectangleLabel.render();
	}
	
	private void roundRectangle(Rectangle bounds, float thickness) {
		extendedShapeRenderer.roundRectangle(bounds, thickness);
	}
	
	private void filledRoundRectangle(Rectangle bounds) {
		extendedShapeRenderer.filledRoundRectangle(bounds);
	}

}
