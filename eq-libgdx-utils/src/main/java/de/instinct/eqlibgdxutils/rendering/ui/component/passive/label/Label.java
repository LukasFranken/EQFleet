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
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
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
	
	private Rectangle physicalTextBounds;
	private EQRectangle backgroundShape;

	public Label(String text) {
		super();
		this.text = text;
		color = new Color(SkinManager.skinColor);
		verticalAlignment = VerticalAlignment.CENTER;
		horizontalAlignment = HorizontalAlignment.CENTER;
		lineSpacing = 5;
		type = FontType.NORMAL;
		
		physicalTextBounds = new Rectangle();
		backgroundShape = EQRectangle.builder()
				.filled(true)
				.bounds(getBounds())
				.build();
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
	protected void updateComponent() {
		physicalTextBounds.set(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		GraphicsUtil.translateToPhysical(physicalTextBounds);
	}

	@Override
	protected void renderComponent() {
		if (backgroundColor != null) {
			backgroundShape.setColor(new Color(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a * getAlpha()));
			Shapes.draw(backgroundShape);
		}
		Color finalColor = new Color(color);
		finalColor.a = finalColor.a * getAlpha();
		FontUtil.setLayoutText("[#" + finalColor.toString() + "]" + text, type);
		GlyphLayout layout = FontUtil.getGlyphLayout();
		float x = 0;
		float y = 0;

		switch (verticalAlignment) {
		case TOP:
			y = physicalTextBounds.y + (physicalTextBounds.height) + (layout.height);
			break;
		case CENTER:
			y = physicalTextBounds.y + (physicalTextBounds.height / 2f) + (layout.height / 2f);
			break;
		case BOTTOM:
			y = physicalTextBounds.y;
			break;
		}

		switch (horizontalAlignment) {
		case LEFT:
			x = physicalTextBounds.x;
			break;
		case CENTER:
			x = physicalTextBounds.x + (physicalTextBounds.width / 2f) - (layout.width / 2f);
			break;
		case RIGHT:
			x = physicalTextBounds.x + physicalTextBounds.width - layout.width;
			break;
		}
		
		FontUtil.draw(new Vector2(x, y), type);
	}

	@Override
	public void dispose() {}
	
}