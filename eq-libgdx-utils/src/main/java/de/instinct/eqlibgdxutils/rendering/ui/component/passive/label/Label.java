package de.instinct.eqlibgdxutils.rendering.ui.component.passive.label;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
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

	public Label(String text) {
		super();
		this.text = text;
		color = new Color(DefaultUIValues.skinColor);
		verticalAlignment = VerticalAlignment.CENTER;
		horizontalAlignment = HorizontalAlignment.CENTER;
		lineSpacing = 5;
	}

	@Override
	protected float calculateWidth() {
		String[] lines = text.split("\n");
		float maxWidth = 0;
		for (String line : lines) {
			float width = FontUtil.getFontTextWidthPx(line);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	protected float calculateHeight() {
		String[] lines = text.split("\n");
		return lines.length * FontUtil.getFontHeightPx() + (lines.length - 1) * lineSpacing;
	}

	@Override
	protected void renderElement() {
		if (backgroundColor != null) {
			TextureManager.draw(TextureManager.createTexture(backgroundColor), getBounds());
		}

		String[] lines = text.split("\n");
		float lineHeight = FontUtil.getFontHeightPx();
		float totalTextHeight = lines.length * lineHeight + (lines.length - 1) * lineSpacing;
		float startY = 0f;

		switch (verticalAlignment) {
		case TOP:
			startY = getBounds().y + getBounds().height;
			break;
		case CENTER:
			startY = getBounds().y + (getBounds().height + totalTextHeight) / 2;
			break;
		case BOTTOM:
			startY = (getBounds().y + lineHeight) + (lines.length - 1) * (lineHeight + lineSpacing);
			break;
		}

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			float lineWidth = FontUtil.getFontTextWidthPx(line);
			float lineX = 0f;
			switch (horizontalAlignment) {
			case LEFT:
				lineX = getBounds().x;
				break;
			case CENTER:
				lineX = getBounds().x + (getBounds().width - lineWidth) / 2;
				break;
			case RIGHT:
				lineX = getBounds().x + getBounds().width - lineWidth;
				break;
			}
			float lineY = startY - i * (lineHeight + lineSpacing);
			FontUtil.draw(color, line, lineX, lineY);
		}
	}

	@Override
	public void dispose() {
	}
}