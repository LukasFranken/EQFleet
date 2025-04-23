package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ColorButton extends Button {

	private String label;
	private Color labelColor;
	private Color color;
	private Color downColor;
	private Color downActiveColor;
	private Color activeColor;
	private Color hoverColor;
	private Color hoverActiveColor;
	private float contentMargin;
	private float fixedHeight;
	private float fixedWidth;

	public ColorButton(String label) {
		super();
		this.label = label;
		labelColor = DefaultUIValues.buttonColor;
		color = DefaultUIValues.buttonDownColor;
		downColor = DefaultUIValues.buttonDownActiveColor;
		downActiveColor = DefaultUIValues.activeColor;
		activeColor = DefaultUIValues.buttonLabelColor;
		hoverColor = DefaultUIValues.buttonHoverColor;
		hoverActiveColor = DefaultUIValues.buttonHoverActiveColor;
		contentMargin = 5;
	}

	@Override
	protected float calculateWidth() {
		return fixedWidth == 0 ? FontUtil.getFontTextWidthPx(label) + (contentMargin * 2) : fixedWidth;
	}

	@Override
	protected float calculateHeight() {
		return fixedHeight == 0 ? FontUtil.getFontHeightPx() + (contentMargin * 2) : fixedHeight;
	}

	@Override
	public void renderElement() {
		super.update();
		TextureManager.draw(TextureManager.createTexture(getButtonColor()), getBounds(), getAlpha());
		labelColor.a = getAlpha();
		FontUtil.draw(labelColor, label, getBounds().x + getBounds().width / 2 - (FontUtil.getFontTextWidthPx(label) / 2), getBounds().y + getBounds().height / 2 + FontUtil.getFontHeightPx() / 2);
	}

	private Color getButtonColor() {
		Color buttonColor = color;
		if (isActive()) {
			buttonColor = activeColor;
		}
		if (isDown()) {
			if (isActive()) {
				buttonColor = downActiveColor;
			} else {
				buttonColor = downColor;
			}
		}
		if (isHovered()) {
			if (isActive()) {
				buttonColor = hoverActiveColor;
			} else {
				buttonColor = hoverColor;
			}
		}
		buttonColor.a = getAlpha();
		return buttonColor;
	}

	@Override
	public void dispose() {

	}

}
