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
		return lines.length * FontUtil.getFontHeightPx(type) + (lines.length - 1) * lineSpacing;
	}
	
	@Override
	protected void updateElement() {
		
	}

	@Override
	protected void renderElement() {
		if (backgroundColor != null) {
			TextureManager.draw(TextureManager.createTexture(backgroundColor), getBounds());
		}

		String[] lines = text.split("\n");
		float lineHeight = FontUtil.getFontHeightPx(type);
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
			float lineWidth = FontUtil.getFontTextWidthPx(line, type);
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
			Color finalColor = new Color(color);
			finalColor.a = finalColor.a * getAlpha();
			FontUtil.draw(finalColor, line, lineX, lineY, type);
		}
	}
	
	public static void drawUnderConstruction(Rectangle bounds) {
		String text = "(under construction)";
		FontUtil.draw(Color.WHITE,
				text,
				(bounds.x + (bounds.width / 2)) - (FontUtil.getFontTextWidthPx(text.length()) / 2),
				(bounds.y + (bounds.height / 2)) - (FontUtil.getFontHeightPx() / 2),
				FontType.NORMAL);
	}

	@Override
	public void dispose() {
	}
	
}