package de.instinct.eqlibgdxutils.rendering.ui.container.list;

import de.instinct.eqlibgdxutils.rendering.ui.container.ElementContainer;
import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ElementStack extends ElementContainer {

	@Override
	protected void updateElementsPosition() {
		for (int i = 0; i < getElements().size(); i++) {
			UIElement currentElement = getElements().get(i);
			currentElement.setPosition(getBounds().x, getBounds().y);
		}
	}

	@Override
	protected float calculateWidth() {
		float width = 0;
		for (UIElement element : getElements()) {
			width = Math.max(width, element.getBounds().width);
		}
		return width;
	}

	@Override
	protected float calculateHeight() {
		float height = 0;
		for (UIElement element : getElements()) {
			height = Math.max(height, element.getBounds().height);
		}
		return height;
	}
	
}
