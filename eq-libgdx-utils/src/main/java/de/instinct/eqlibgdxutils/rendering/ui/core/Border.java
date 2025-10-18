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
	
	public void render() {
		Color finalColor = new Color(color);
		finalColor.a *= alpha;
		
		Shapes.draw(EQRectangle.builder()
				.color(finalColor)
				.bounds(bounds)
				.thickness(size)
				.build());
	}

}
