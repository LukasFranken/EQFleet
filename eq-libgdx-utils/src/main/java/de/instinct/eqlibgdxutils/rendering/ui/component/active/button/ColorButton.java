package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ColorButton extends Button {

	private Label label;
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

	public ColorButton(String text) {
		super();
		label = new Label(text);
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
		return fixedWidth == 0 ? FontUtil.getFontTextWidthPx(label.getText(), FontType.NORMAL) + (contentMargin * 2) : fixedWidth;
	}

	@Override
	protected float calculateHeight() {
		return fixedHeight == 0 ? FontUtil.getFontHeightPx() + (contentMargin * 2) : fixedHeight;
	}

	@Override
	public void renderElement() {
		super.update();
		TextureManager.draw(TextureManager.createTexture(getButtonColor()), getBounds(), getAlpha());
		label.setAlpha(getAlpha());
		label.setBounds(getBounds());
		label.render();
	}

	private Color getButtonColor() {
		Color buttonColor = new Color(color);
		if (isActive()) {
			buttonColor = new Color(activeColor);
		}
		if (isDown()) {
			if (isActive()) {
				buttonColor = new Color(downActiveColor);
			} else {
				buttonColor = new Color(downColor);
			}
		}
		if (isHovered()) {
			if (isActive()) {
				buttonColor = new Color(hoverActiveColor);
			} else {
				buttonColor = new Color(hoverColor);
			}
		}
		buttonColor.a = buttonColor.a * getAlpha();
		return buttonColor;
	}

	@Override
	public void dispose() {

	}

}
