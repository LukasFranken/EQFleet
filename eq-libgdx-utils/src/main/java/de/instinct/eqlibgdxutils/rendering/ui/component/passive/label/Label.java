package de.instinct.eqlibgdxutils.rendering.ui.component.passive.label;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Label extends Component {

	private String text;
	private Color color;
	private VerticalAlignment verticalAlignment;
	private HorizontalAlignment horizontalAlignment;
	private Color backgroundColor;
	private float lineSpacing;
	private FontType type;

	public Label(String text) {
		super();
		this.text = text;
		color = new Color(SkinManager.skinColor);
		verticalAlignment = VerticalAlignment.CENTER;
		horizontalAlignment = HorizontalAlignment.CENTER;
		lineSpacing = 5;
		type = FontType.NORMAL;
	}

	@Override
	public float calculateWidth() {
		String[] lines = text.split("\n");
		float maxWidth = 0;
		for (String line : lines) {
			float width = FontUtil.getNormalizedFontTextWidthPx(line, type);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	public float calculateHeight() {
		String[] lines = text.split("\n");
		return lines.length * FontUtil.getNormalizedFontHeightPx(type) + (lines.length - 1) * lineSpacing;
	}
	
	@Override
	protected void updateComponent() {}

	@Override
	protected void renderComponent() {
		Rectangle displayAdjustedBounds = getScreenScaleAdjustedBounds();
		if (backgroundColor != null) {
			TextureManager.draw(TextureManager.createTexture(backgroundColor), displayAdjustedBounds, getAlpha());
		}

		String[] lines = text.split("\n");
		float lineHeight = FontUtil.getFontHeightPx(type);
		float totalTextHeight = lines.length * lineHeight + (lines.length - 1) * lineSpacing;
		float startY = 0f;

		switch (verticalAlignment) {
		case TOP:
			startY = displayAdjustedBounds.y + displayAdjustedBounds.height;
			break;
		case CENTER:
			startY = displayAdjustedBounds.y + (displayAdjustedBounds.height + totalTextHeight) / 2;
			break;
		case BOTTOM:
			startY = (displayAdjustedBounds.y + lineHeight) + (lines.length - 1) * (lineHeight + lineSpacing);
			break;
		}

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			float lineWidth = FontUtil.getFontTextWidthPx(line, type);
			float lineX = 0f;
			switch (horizontalAlignment) {
			case LEFT:
				lineX = displayAdjustedBounds.x;
				break;
			case CENTER:
				lineX = displayAdjustedBounds.x + (displayAdjustedBounds.width - lineWidth) / 2;
				break;
			case RIGHT:
				lineX = displayAdjustedBounds.x + displayAdjustedBounds.width - lineWidth;
				break;
			}
			float lineY = startY - i * (lineHeight + lineSpacing);
			Color finalColor = new Color(color);
			finalColor.a = finalColor.a * getAlpha();
			FontUtil.draw(finalColor, line, lineX, lineY, type);
		}
	}

	@Override
	public void dispose() {}
	
}