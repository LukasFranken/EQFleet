package de.instinct.eqlibgdxutils.rendering.ui.core;

import com.badlogic.gdx.math.Rectangle;

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

	protected abstract void renderElement();

	public abstract void dispose();

}
