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
	
	private String texturePath;
	private String textureName;

	public Image(Texture texture) {
		super();
		this.texture = texture;
		getBounds().width = texture.getWidth();
		getBounds().height = texture.getHeight();
	}
	
	public Image() {
		super();
	}

	public void updateTexture(String texturePath, String textureName) {
		if (this.texturePath != null && this.textureName != null) {
			if (this.texturePath.contentEquals(texturePath) && this.textureName.contentEquals(textureName)) return;
		}
		this.texturePath = texturePath;
		this.textureName = textureName;
		texture = TextureManager.getTexture(texturePath, textureName);
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
		if (texture != null) TextureManager.draw(texture, getBounds(), getAlpha());
	}

	@Override
	public void dispose() {
		if (texture != null) TextureManager.dispose(texture);
	}

}
