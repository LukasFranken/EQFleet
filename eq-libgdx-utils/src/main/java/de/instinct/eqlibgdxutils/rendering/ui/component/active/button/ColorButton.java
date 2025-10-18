package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
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
	
	private boolean glowAnimation;
	private float currentGlow;

	public ColorButton(String text) {
		super();
		label = new Label(text);
		labelColor = Color.GRAY;
		color = Color.DARK_GRAY;
		downColor = new Color(SkinManager.darkestSkinColor);
		downActiveColor = new Color(SkinManager.darkerSkinColor);
		activeColor = Color.BLACK;
		hoverColor = Color.LIGHT_GRAY;
		hoverActiveColor = new Color(SkinManager.skinColor);
		contentMargin = 5;
	}

	@Override
	public float calculateWidth() {
		return fixedWidth == 0 ? FontUtil.getFontTextWidthPx(label.getText().length()) + (contentMargin * 2) : fixedWidth;
	}

	@Override
	public float calculateHeight() {
		return fixedHeight == 0 ? FontUtil.getFontHeightPx() + (contentMargin * 2) : fixedHeight;
	}
	
	@Override
	protected void updateButton() {
		label.setAlpha(getAlpha());
		label.setBounds(getBounds());
		currentGlow += Gdx.graphics.getDeltaTime();
		if (currentGlow > 1f) {
			currentGlow = 0f;
		}
	}

	@Override
	public void renderComponent() {
		if (glowAnimation && !isHovered() && !isDown()) {
			TextureManager.draw(TextureManager.createTexture(new Color(downColor)), getBounds(), getAlpha() * (0.6f * (float) Math.sin(currentGlow * Math.PI)));
		} else {
			TextureManager.draw(TextureManager.createTexture(getButtonColor()), getBounds(), getAlpha());
		}
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
