package de.instinct.eqlibgdxutils.rendering.ui.component.passive.image;

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
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	protected void updateComponent() {
		
	}
	
	@Override
	public void renderComponent() {
		TextureManager.draw(texture, getBounds(), getAlpha());
	}

	@Override
	public void dispose() {
		TextureManager.dispose(texture);
	}

}
