package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

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
	
	private float margin;

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
		Rectangle imageBounds = new Rectangle(getBounds());
		imageBounds.x += margin;
		imageBounds.y += margin;
		imageBounds.width -= 2 * margin;
		imageBounds.height -= 2 * margin;
		TextureManager.draw(getButtonTexture(), imageBounds, getAlpha());
	}

	private Texture getButtonTexture() {
		Texture currentTexture = imageTexture;
		if (isActive() && activeTexture != null) {
			currentTexture = activeTexture;
		}
		if (isHovered() && hoverTexture != null) {
			currentTexture = hoverTexture;
		}
		if (isDown() && downTexture != null) {
			currentTexture = downTexture;
		}
		return currentTexture;
	}

	@Override
	public void dispose() {
		imageTexture.dispose();
	}

}
