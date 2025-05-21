package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.graphics.Texture;

import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ImageButton extends Button {

	private Texture imageTexture;
	private Texture hoverTexture;
	private Texture activeTexture;
	private Texture downTexture;

	@Override
	protected float calculateWidth() {
		return imageTexture.getWidth();
	}

	@Override
	protected float calculateHeight() {
		return imageTexture.getHeight();
	}

	@Override
	public void renderElement() {
		super.update();
		TextureManager.draw(getButtonTexture(), getBounds(), getAlpha());
	}

	private Texture getButtonTexture() {
		Texture currentTexture = imageTexture;
		if (isActive()) {
			currentTexture = activeTexture;
		}
		if (isHovered()) {
			currentTexture = hoverTexture;
		}
		if (isDown()) {
			currentTexture = downTexture;
		}
		return currentTexture;
	}

	@Override
	public void dispose() {
		imageTexture.dispose();
	}

}
