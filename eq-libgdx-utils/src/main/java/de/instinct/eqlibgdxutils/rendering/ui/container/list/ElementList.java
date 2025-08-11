package de.instinct.eqlibgdxutils.rendering.ui.container.list;

import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.rendering.ui.container.ElementContainer;
import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ElementList extends ElementContainer {

	private float margin;

	@Override
	protected void updateElementsPosition() {
		Vector2 nextElementPosition = new Vector2(getBounds().x, getBounds().y + getBounds().height);
		for (int i = 0; i < getElements().size(); i++) {
			UIElement currentElement = getElements().get(i);
			nextElementPosition.y -= currentElement.getBounds().height;
			currentElement.setPosition(nextElementPosition.x, nextElementPosition.y);
			nextElementPosition.y -= margin;
		}
	}

	@Override
	public float calculateWidth() {
		float width = 0;
		for (UIElement element : getElements()) {
			element.update();
			width = Math.max(width, element.getBounds().width);
		}
		return width;
	}

	@Override
	public float calculateHeight() {
		float height = 0;
		for (int i = 0; i < getElements().size(); i++) {
			getElements().get(i).update();
			height += getElements().get(i).getBounds().height;
			if (i > 0) {
				height += margin;
	        }
		}
		return height;
	}

}
