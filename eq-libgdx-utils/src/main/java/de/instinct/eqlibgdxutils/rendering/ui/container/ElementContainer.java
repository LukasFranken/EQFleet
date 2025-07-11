package de.instinct.eqlibgdxutils.rendering.ui.container;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class ElementContainer extends UIElement {

	private List<UIElement> elements;

	public ElementContainer() {
		super();
		elements = new ArrayList<>();
	}

	@Override
	public void updateElement() {
		for (UIElement element : elements) {
			element.update();
		}
		updateElementsPosition();
	}

	protected abstract void updateElementsPosition();

	@Override
	public void renderElement() {
		for (UIElement element : elements) {
			element.render();
		}
	}

	@Override
	public void dispose() {
		for (UIElement element : elements) {
			element.dispose();
		}
	}
	
	public void setPosition(float x, float y) {
		getBounds().x = x;
		getBounds().y = y;
	}

}
