package de.instinct.eqlibgdxutils.rendering.ui.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
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
	private Color borderColor;
	private boolean decorated;
	private Rectangle bounds;

	private int rendercount = 0;

    public BaseModule() {
		transparency = 1f;
		elementMargin = 10f;
		backgroundColor = Color.BLACK;
		borderColor = DefaultUIValues.skinColor;
		decorated = true;
		bounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void update() {
    	updateContent();
    	super.update();
    	updatePosition();
    	restrictTo(bounds);
    	updateContentPosition();
    }

    public abstract void updatePosition();

    public abstract void updateContent();

    public abstract void updateContentPosition();

	@Override
	public void renderElement() {
		renderBackground();
		renderBorder();
		renderContent();
	}

	protected abstract void renderContent();

	private void renderBackground() {
		backgroundColor.a = transparency;
		TextureManager.draw(TextureManager.createTexture(backgroundColor), super.getBounds());
	}

	private void renderBorder() {
		Rectangle topBorderBounds = new Rectangle(super.getBounds().x, super.getBounds().y + super.getBounds().height - 2, super.getBounds().width, 2);
		TextureManager.draw(TextureManager.createTexture(borderColor), topBorderBounds);
		Rectangle rightBorderBounds = new Rectangle(super.getBounds().x + super.getBounds().width - 2, super.getBounds().y, 2, super.getBounds().height);
		TextureManager.draw(TextureManager.createTexture(borderColor), rightBorderBounds);
		Rectangle bottomBorderBounds = new Rectangle(super.getBounds().x, super.getBounds().y, super.getBounds().width, 2);
		TextureManager.draw(TextureManager.createTexture(borderColor), bottomBorderBounds);
		Rectangle leftBorderBounds = new Rectangle(super.getBounds().x, super.getBounds().y, 2, super.getBounds().height);
		TextureManager.draw(TextureManager.createTexture(borderColor), leftBorderBounds);
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
