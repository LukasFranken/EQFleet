package de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.EQShape;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EQRectangle extends EQShape {
	
	private Rectangle bounds;
	private String label;
	private boolean round;
	private float thickness;

}
