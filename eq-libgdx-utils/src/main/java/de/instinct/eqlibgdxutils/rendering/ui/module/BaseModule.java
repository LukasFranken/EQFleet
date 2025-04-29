package de.instinct.eqlibgdxutils.rendering.ui.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseModule extends UIElement {

	private float transparency;
	private float elementMargin;
	private Color backgroundColor;
	private boolean decorated;
	private Border defaultBorder;

	private int rendercount = 0;

    public BaseModule() {
		transparency = 1f;
		elementMargin = 10f;
		backgroundColor = Color.BLACK;
		decorated = false;
		setBounds(new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		defaultBorder = new Border();
		defaultBorder.setSize(2);
		defaultBorder.setColor(DefaultUIValues.darkestSkinColor);
    }

    @Override
    public void update() {
    	updateContent();
    	super.update();
    	updatePosition();
    	restrictTo(getBounds());
    	updateContentPosition();
    }

    public abstract void updateContent();
    
    public abstract void updatePosition();

    public abstract void updateContentPosition();

	@Override
	public void renderElement() {
		if (decorated) {
			setBorder(defaultBorder);
			renderBackground();
		} else {
			setBorder(null);
		}
		renderContent();
	}

	protected abstract void renderContent();

	private void renderBackground() {
		backgroundColor.a = transparency;
		TextureManager.draw(TextureManager.createTexture(backgroundColor), super.getBounds());
	}

	private void restrictTo(Rectangle restrictionBounds) {
		if (super.getBounds().x + super.getBounds().width > restrictionBounds.x + restrictionBounds.width) {
			super.getBounds().x = super.getBounds().x - ((super.getBounds().x + super.getBounds().width) - (restrictionBounds.x + restrictionBounds.width));
		}
		if (super.getBounds().y + super.getBounds().height > restrictionBounds.y + restrictionBounds.height) {
			super.getBounds().y = super.getBounds().y - ((super.getBounds().y + super.getBounds().height) - (restrictionBounds.y + restrictionBounds.height));
		}
	}

}
