package de.instinct.eqfleet.menu.common.components.label;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LabelStackConfiguration {
	
	private String tag;
	private String value;
	private float width;
	private Color colorTag;
	private Color colorValue;
	private FontType type;

}
