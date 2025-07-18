package de.instinct.eqfleet.menu.postgame.model;

import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostGameElement {
	
	private UIElement uiElement;
	private float duration;
	private float elapsed;
	private AnimationAction animationAction;

}
