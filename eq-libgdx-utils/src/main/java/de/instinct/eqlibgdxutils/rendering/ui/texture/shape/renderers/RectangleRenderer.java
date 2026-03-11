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
	
	private Rectangle virtualWorkingBounds;
	private Rectangle physicalWorkingBounds;
	private Rectangle partialRectWorkingBounds;
	
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
		virtualWorkingBounds = new Rectangle();
		physicalWorkingBounds = new Rectangle();
		partialRectWorkingBounds = new Rectangle();
	}
	
	public void render(EQRectangle rectangle) {
		virtualWorkingBounds.set(rectangle.getBounds());
		physicalWorkingBounds.set(rectangle.getBounds());
		if (rectangle.getProjectionMatrix() != null) {
			extendedShapeRenderer.setProjectionMatrix(rectangle.getProjectionMatrix());
		} else {
			GraphicsUtil.translateToPhysical(physicalWorkingBounds);
		}
		
		if (rectangle.getLabel() != null) {
			labeledRectangle(rectangle.getLabel(), rectangle.getColor());
		} else {
			float thickness = rectangle.getProjectionMatrix() == null ? GraphicsUtil.getScaleFactor() * rectangle.getThickness() : rectangle.getThickness();
			if (rectangle.isRound()) {
				if (rectangle.getThickness() == 0) {
					filledRoundRectangle();
				} else {
					roundRectangle(thickness);
				}
			} else {
				if (rectangle.isFilled()) {
					rectangle();
				} else {
					rectangle(thickness);
				}
				if (rectangle.getGlowConfig() != null) glowRenderer.rectangle(physicalWorkingBounds, rectangle.getThickness(), rectangle.getGlowConfig());
			}
		}
	}
	
	private void rectangle() {
		extendedShapeRenderer.rect(physicalWorkingBounds);
	}
	
	private void rectangle(float thickness) {
		extendedShapeRenderer.rect(physicalWorkingBounds, thickness);
	}
	
	private void labeledRectangle(String text, Color color) {
		float offset = 20;
		float labelWidth = FontUtil.getFontTextWidthPx(text.length(), FontType.SMALL) + offset;
		
		float adjustedOffset = GraphicsUtil.getScaleFactor() * offset;
		float adjustedLabelWidth = GraphicsUtil.getScaleFactor() * labelWidth;

		partialRectWorkingBounds.set(physicalWorkingBounds.x, physicalWorkingBounds.y, physicalWorkingBounds.width, physicalWorkingBounds.height - (adjustedOffset / 2));
		extendedShapeRenderer.partialRect(partialRectWorkingBounds, adjustedLabelWidth, adjustedOffset, 1f);
		extendedShapeRenderer.end();
		
		labeledRectangleLabel.setText(text);
		labeledRectangleLabel.setBounds(virtualWorkingBounds.x + offset, virtualWorkingBounds.y + virtualWorkingBounds.height - offset, labelWidth, offset);
		labeledRectangleLabel.setColor(color);
		labeledRectangleLabel.getBorder().setColor(color);
		labeledRectangleLabel.render();
	}
	
	private void roundRectangle(float thickness) {
		extendedShapeRenderer.roundRectangle(physicalWorkingBounds, thickness);
	}
	
	private void filledRoundRectangle() {
		extendedShapeRenderer.filledRoundRectangle(physicalWorkingBounds);
	}

}
