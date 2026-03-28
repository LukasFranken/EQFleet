package de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.types;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.LabeledButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LabeledImageButton extends LabeledButton {

	private Image image;

	public LabeledImageButton(Texture imageTexture, String message) {
		super();
		super.getLabel().setText(message);
		image = new Image(imageTexture);
	}

	@Override
	protected void updateContent(Rectangle contentBounds) {
		image.setBounds(contentBounds);
		image.setAlpha(getAlpha());
	}
	
	@Override
	protected void setContentBorder(Border modelBorder) {
		image.setBorder(modelBorder);
	}

	@Override
	protected void renderContent() {
		image.render();
	}

	@Override
	protected void disposeContent() {
		image.dispose();
	}

}
