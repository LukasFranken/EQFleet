package de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes;

import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.EQShape;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EQCircle extends EQShape {
	
	@Default
	private Vector2 position = new Vector2();
	private float radius;
	
	public void setPosition(Vector2 position) {
		this.position.set(position);
	}

}
