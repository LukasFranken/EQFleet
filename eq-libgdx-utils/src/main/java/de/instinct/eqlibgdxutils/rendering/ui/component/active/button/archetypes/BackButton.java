package de.instinct.eqlibgdxutils.rendering.ui.component.active.button.archetypes;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ImageButton;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BackButton extends ImageButton {

	public BackButton() {
		super();
		setImageTexture(TextureManager.getTexture("ui/image", "arrowicon"));
		setHoverTexture(TextureManager.getTexture("ui/image", "arrowiconactive"));
		setDownTexture(TextureManager.getTexture("ui/image", "arrowicondown"));
		setActiveTexture(TextureManager.getTexture("ui/image", "arrowiconhover"));
	}

}
