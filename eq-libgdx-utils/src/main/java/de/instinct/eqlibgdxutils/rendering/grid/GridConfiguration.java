package de.instinct.eqlibgdxutils.rendering.grid;

import com.badlogic.gdx.graphics.Color;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class GridConfiguration {
	
	@Default
	private Color lineColor = new Color(0f, 0.5f, 0f, 0.2f);
	
	@Default
	private float lineThickness = 1f;
	
	@Default
	private float step = 50f;

}
