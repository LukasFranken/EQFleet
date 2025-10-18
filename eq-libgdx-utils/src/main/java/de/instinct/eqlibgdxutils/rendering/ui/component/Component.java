package de.instinct.eqlibgdxutils.rendering.ui.component;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class Component extends UIElement {
	
	private Border border;

	@Override
	public void updateElement() {
		updateComponent();
	}
	
	@Override
	protected void renderElement() {
		renderComponent();
		if (border != null) {
			border.setAlpha(getAlpha());
			border.setBounds(getBounds());
			border.render();
		}
	}
	
	protected abstract void renderComponent();

	protected abstract void updateComponent();
	
	public void debug() {
		border = new Border();
		border.setColor(Color.ORANGE);
		border.setAlpha(1f);
		border.setBounds(getBounds());
		border.setSize(2f);
	}

}
