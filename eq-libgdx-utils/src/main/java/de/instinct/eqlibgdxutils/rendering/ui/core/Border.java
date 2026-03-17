package de.instinct.eqlibgdxutils.rendering.ui.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;

@Data
public class Border {

	private float size;
	private Color color;
	private Rectangle bounds;
	private float alpha;
	
	private EQRectangle borderShape;
	
	public Border() {
		borderShape = EQRectangle.builder()
				.color(new Color(0, 0, 0, 1))
				.bounds(new Rectangle())
				.thickness(1f)
				.build();
	}
	
	public void render() {
		borderShape.getColor().set(color);
		borderShape.getColor().a *= alpha;
		borderShape.getBounds().set(bounds);
		borderShape.setThickness(size);
		
		Shapes.draw(borderShape);
	}

}
