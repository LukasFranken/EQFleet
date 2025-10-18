package de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes;

import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.EQShape;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EQArc extends EQShape {
	
	private Vector2 position;
	private float innerRadius;
	private float outerRadius;
	private float startAngle;
	private float degrees;

}
