package de.instinct.eqlibgdxutils.rendering.ui.component.passive;

import com.badlogic.gdx.graphics.Texture;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Image extends Component {

	private Texture texture;

	public Image(Texture texture) {
		super();
		this.texture = texture;
		getBounds().width = texture.getWidth();
		getBounds().height = texture.getHeight();
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
	public void renderElement() {
		TextureManager.draw(texture, getBounds(), getAlpha());
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

}
