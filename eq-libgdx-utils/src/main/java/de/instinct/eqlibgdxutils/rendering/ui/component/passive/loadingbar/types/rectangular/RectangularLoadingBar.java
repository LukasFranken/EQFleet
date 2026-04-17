package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.LoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public abstract class RectangularLoadingBar extends LoadingBar {
	
	private EQRectangle backgroundShape;
	
	public RectangularLoadingBar() {
		super();
		Border defaultBorder = new Border();
		defaultBorder.setColor(new Color(Color.GRAY));
		defaultBorder.setSize(2f);
		setBorder(defaultBorder);
	}
	
	@Override
	protected void renderComponent() {
		renderBackground();
		renderContent();
		renderLabel();
	}

	private void renderBackground() {
		if (backgroundShape != null) Shapes.draw(backgroundShape);
	}

	protected abstract void renderContent();
	
	protected abstract void renderLabel();
	
}
