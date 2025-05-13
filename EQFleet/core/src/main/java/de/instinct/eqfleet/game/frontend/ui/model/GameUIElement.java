package de.instinct.eqfleet.game.frontend.ui.model;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameUIElement<T extends UIElement> {
	
	private String tag;
	private boolean visible;
	private T element;
	private Rectangle bounds;
	private Action initAction;
	private Action updateAction;
	private Action postRenderAction;

}
