package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LayeredImageButton extends Button {

	private List<Texture> imageTextures;
	
	public LayeredImageButton() {
		super();
	}

	@Override
	public float calculateWidth() {
		return 0;
	}

	@Override
	public float calculateHeight() {
		return 0;
	}
	
	@Override
	protected void updateButton() {
		
	}

	@Override
	public void renderComponent() {
		
	}

	@Override
	public void dispose() {
		for (Texture texture : imageTextures) {
			texture.dispose();
		}
	}

}