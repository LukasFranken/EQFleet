package de.instinct.eqlibgdxutils.rendering.ui.component.passive.label;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.GraphicsUtil;
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
			float width = FontUtil.getFontTextWidthPx(line.length(), type);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	public float calculateHeight() {
		String[] lines = text.split("\n");
		return lines.length * FontUtil.getFontHeightPx(type) + (lines.length - 1) * lineSpacing;
	}
	
	@Override
	protected void updateComponent() {}

	@Override
	protected void renderComponent() {
		Rectangle bounds = GraphicsUtil.scaleFactorAdjusted(getBounds());
		if (text.contentEquals("SOLO CONQUEST")) {
			System.out.println("color " + backgroundColor);
			System.out.println(bounds);
			System.out.println("alpha " + getAlpha());
		}
		if (backgroundColor != null) {
			TextureManager.draw(TextureManager.createTexture(backgroundColor), bounds, getAlpha());
		}
		
		Color finalColor = new Color(color);
		finalColor.a = finalColor.a * getAlpha();
		FontUtil.setLayoutText("[#" + finalColor.toString() + "]" + text, type);
		GlyphLayout layout = FontUtil.getGlyphLayout();
		float x = 0;
		float y = 0;

		switch (verticalAlignment) {
		case TOP:
			y = bounds.y + (bounds.height) + (layout.height);
			break;
		case CENTER:
			y = bounds.y + (bounds.height / 2f) + (layout.height / 2f);
			break;
		case BOTTOM:
			y = bounds.y;
			break;
		}

		switch (horizontalAlignment) {
		case LEFT:
			x = bounds.x;
			break;
		case CENTER:
			x = bounds.x + (bounds.width / 2f) - (layout.width / 2f);
			break;
		case RIGHT:
			x = bounds.x + bounds.width - layout.width;
			break;
		}
		
		FontUtil.draw(new Vector2(x, y), type);
	}

	@Override
	public void dispose() {}
	
}