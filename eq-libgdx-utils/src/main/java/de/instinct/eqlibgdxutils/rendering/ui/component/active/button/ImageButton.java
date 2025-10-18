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
	private Rectangle imageBounds;
	
	public ImageButton() {
		super();
	}

	@Override
	public float calculateWidth() {
		return imageTexture.getWidth();
	}

	@Override
	public float calculateHeight() {
		return imageTexture.getHeight();
	}

	@Override
	protected void updateButton() {
		imageBounds = new Rectangle(getBounds());
		imageBounds.x += margin;
		imageBounds.y += margin;
		imageBounds.width -= 2 * margin;
		imageBounds.height -= 2 * margin;
	}
	
	@Override
	public void renderComponent() {
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
