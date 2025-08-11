package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LabeledImageButton extends Button {

	private Texture imageTexture;
	private Label messageLabel;
	private float contentMargin;
	private float spacing;

	private Texture hoverTexture;
	private Texture clickTexture;
	
	private Rectangle imageBounds;

	public LabeledImageButton(Texture imageTexture, String message) {
		super();
		this.imageTexture = imageTexture;
		spacing = 20f;
		messageLabel = new Label(message);
		messageLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		Color hoverColor = new Color(SkinManager.darkerSkinColor);
		hoverColor.a = 0.4f;
		hoverTexture = TextureManager.createTexture(hoverColor);
		Color clickColor = new Color(SkinManager.skinColor);
		clickColor.a = 0.4f;
		clickTexture = TextureManager.createTexture(clickColor);
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}
	
	@Override
	protected void updateButton() {
		imageBounds = calculateImageBounds(getScreenScaleAdjustedBounds());
		messageLabel.setPosition(getBounds().x + getBounds().width + contentMargin + spacing, getBounds().y);
		messageLabel.setFixedWidth(getBounds().width - getBounds().width - (contentMargin * 2) - spacing);
		messageLabel.setFixedHeight(getBounds().height);
	}

	@Override
	protected void renderComponent() {
		TextureManager.draw(imageTexture, imageBounds);
		messageLabel.render();
		if (isHovered()) {
			TextureManager.draw(hoverTexture, getScreenScaleAdjustedBounds());
		}
		if (isDown()) {
			TextureManager.draw(clickTexture, getScreenScaleAdjustedBounds());
		}
	}

	private Rectangle calculateImageBounds(Rectangle bounds) {
		return new Rectangle(
				bounds.x + contentMargin,
				bounds.y + contentMargin,
				adjustedWidth(bounds),
				bounds.height - (contentMargin * 2));
	}

	private float adjustedWidth(Rectangle bounds) {
		return imageTexture.getWidth() / (imageTexture.getHeight() / (bounds.height - (contentMargin * 2)));
	}

	@Override
	public void dispose() {
		imageTexture.dispose();
	}

}
