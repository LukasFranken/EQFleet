package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.LoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public abstract class RectangularLoadingBar extends LoadingBar {

	private Texture background;
	
	public RectangularLoadingBar() {
		super();
		Border defaultBorder = new Border();
		defaultBorder.setColor(new Color(Color.BLACK));
		defaultBorder.setSize(2);
		setBorder(defaultBorder);
		background = TextureManager.createTexture(Color.GRAY);
	}
	
	@Override
	protected void renderElement() {
		renderBackground();
		renderContent();
		renderLabel();
	}

	private void renderBackground() {
		TextureManager.draw(getBackground(), getBounds());
	}

	protected abstract void renderContent();
	
	protected abstract void renderLabel();
	
}
