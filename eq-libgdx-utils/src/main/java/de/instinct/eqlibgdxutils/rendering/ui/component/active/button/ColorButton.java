package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
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
	
	private EQRectangle backgroundShape;

	public ColorButton(String text) {
		super();
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(SkinManager.skinColor));
		buttonBorder.setSize(2);
		setBorder(buttonBorder);
		label = new Label(text);
		labelColor = new Color(SkinManager.skinColor);
		color = Color.BLACK;
		downColor = new Color(SkinManager.lighterSkinColor);
		downActiveColor = new Color(SkinManager.darkerSkinColor);
		activeColor = Color.BLACK;
		hoverColor = new Color(SkinManager.darkerSkinColor);
		hoverActiveColor = new Color(SkinManager.skinColor);
		contentMargin = 5;
		backgroundShape = EQRectangle.builder()
				.bounds(getBounds())
				.color(new Color())
				.filled(true)
				.build();
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
		if (glowAnimation && !isHovered() && !isDown()) {
			currentGlow += Gdx.graphics.getDeltaTime();
			if (currentGlow > 1f) {
				currentGlow = 0f;
			}
			backgroundShape.getColor().set(downColor.r, downColor.g, downColor.b, downColor.a * getAlpha() * (0.6f * (float) Math.sin(currentGlow * Math.PI)));
		} else {
			backgroundShape.getColor().set(getButtonColor().r, getButtonColor().g, getButtonColor().b, getButtonColor().a * getAlpha());
		}
	}

	@Override
	public void renderComponent() {
		Shapes.draw(backgroundShape);
		label.render();
	}

	private Color getButtonColor() {
		if (isActive()) {
			return activeColor;
		}
		if (isDown()) {
			if (isActive()) {
				return downActiveColor;
			} else {
				return downColor;
			}
		}
		if (isHovered()) {
			if (isActive()) {
				return hoverActiveColor;
			} else {
				return hoverColor;
			}
		}
		return color;
	}

	@Override
	public void dispose() {
		label.dispose();
	}

}
