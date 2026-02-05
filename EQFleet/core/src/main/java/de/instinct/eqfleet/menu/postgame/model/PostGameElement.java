package de.instinct.eqfleet.menu.postgame.model;

import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;

public interface PostGameElement {
	
	UIElement getUiElement();
	float getDuration();
	float getElapsed();
	void setElapsed(float elapsed);
	AnimationAction getAnimationAction();
	boolean isHalted();

}
