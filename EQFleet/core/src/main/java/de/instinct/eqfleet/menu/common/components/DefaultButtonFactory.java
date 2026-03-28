package de.instinct.eqfleet.menu.common.components;

import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ImageButton;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class DefaultButtonFactory {

	public static ColorButton colorButton(String label, Action clickAction) {
		ColorButton colorButton = new ColorButton(label);
		colorButton.setAction(clickAction);
		return colorButton;
	}
	
	public static ImageButton backButton(Action backAction) {
		ImageButton backButton = new ImageButton();
		backButton.setImageTexture(TextureManager.getTexture("ui/image", "arrowicon"));
		backButton.setFixedHeight(20);
		backButton.setFixedWidth(20);
		backButton.setAction(backAction);
		return backButton;
	}

}
