package de.instinct.eqlibgdxutils.rendering.ui.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class UIElement {

	@Setter(AccessLevel.NONE)
	private Rectangle bounds;
	
	private float fixedWidth;
	private float fixedHeight;

	private float alpha;

	private Border border;

	private boolean debug;

	public UIElement() {
		alpha = 1f;
		bounds = new Rectangle();
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
		if (bounds != null) {
			fixedWidth = bounds.width;
			fixedHeight = bounds.height;
		} else {
			fixedWidth = 0f;
			fixedHeight = 0f;
		}
	}

	protected abstract float calculateWidth();

	protected abstract float calculateHeight();

	public void setPosition(float x, float y) {
		bounds.x = x;
		bounds.y = y;
	}

	public void render() {
		update();
		renderElement();
		if (debug) {
			border = new Border();
			border.setColor(Color.ORANGE);
			border.setSize(2f);
		}
		if (border != null && border.getColor() != null) {
			drawBorder();
		}
	}
	
	public void update() {
		if (fixedWidth > 0) {
			bounds.width = fixedWidth;
		} else {
			bounds.width = calculateWidth();
		}

		if (fixedHeight > 0) {
			bounds.height = fixedHeight;
		} else {
			bounds.height = calculateHeight();
		}
		updateElement();
	}

	protected abstract void updateElement();

	public void debug() {
		debug = true;
	}

	private void drawBorder() {
		border.getColor().a = alpha;
		TextureManager.draw(TextureManager.createTexture(border.getColor()), new Rectangle(getBounds().getX(), getBounds().getY(), border.getSize(), getBounds().getHeight()));
		TextureManager.draw(TextureManager.createTexture(border.getColor()), new Rectangle(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), border.getSize()));
		TextureManager.draw(TextureManager.createTexture(border.getColor()), new Rectangle(getBounds().getX() + getBounds().getWidth() - border.getSize(), getBounds().getY(), border.getSize(), getBounds().getHeight()));
		TextureManager.draw(TextureManager.createTexture(border.getColor()), new Rectangle(getBounds().getX(), getBounds().getY() + getBounds().getHeight() - border.getSize(), getBounds().getWidth(), border.getSize()));
	}

	protected abstract void renderElement();

	public abstract void dispose();

}
