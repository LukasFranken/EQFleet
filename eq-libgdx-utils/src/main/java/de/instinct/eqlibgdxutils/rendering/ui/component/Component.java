package de.instinct.eqlibgdxutils.rendering.ui.component;

import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class Component extends UIElement {

	@Override
	public void render() {
		super.render();
	}

}
