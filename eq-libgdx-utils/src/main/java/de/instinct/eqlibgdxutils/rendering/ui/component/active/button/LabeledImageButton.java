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
	protected float calculateWidth() {
		return getBounds().width;
	}

	@Override
	protected float calculateHeight() {
		return getBounds().height;
	}

	@Override
	protected void renderElement() {
		Rectangle imageBounds = calculateImageBounds();
		TextureManager.draw(imageTexture, imageBounds);
		messageLabel.setPosition(getBounds().x + imageBounds.width + contentMargin + spacing, getBounds().y);
		messageLabel.setFixedWidth(getBounds().width - imageBounds.width - (contentMargin * 2) - spacing);
		messageLabel.setFixedHeight(getBounds().height);
		messageLabel.render();
		if (isHovered()) {
			TextureManager.draw(hoverTexture, getBounds());
		}
		if (isDown()) {
			TextureManager.draw(clickTexture, getBounds());
		}
	}

	public Rectangle calculateImageBounds() {
		return new Rectangle(getBounds().x + contentMargin, getBounds().y + contentMargin, adjustedWidth(), getBounds().height - (contentMargin * 2));
	}

	private float adjustedWidth() {
		return imageTexture.getWidth() / (imageTexture.getHeight() / (getBounds().height - (contentMargin * 2)));
	}

	@Override
	public void dispose() {
		imageTexture.dispose();
	}

}
