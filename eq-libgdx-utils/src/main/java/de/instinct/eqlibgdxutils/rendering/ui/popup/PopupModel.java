package de.instinct.eqlibgdxutils.rendering.ui.popup;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PopupModel {
	
	private Rectangle bounds;
	private Label titleLabel;
	private Popup popup;
	private String windowTextureTag;
	private String windowTitlebarTextureTag;

}
