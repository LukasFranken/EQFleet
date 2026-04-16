package de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;

import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;
import lombok.Data;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class EQShape {
	
	private Matrix4 projectionMatrix;
	private EQGlowConfig glowConfig;
	
	@Default
	private Color color = new Color();
	
	public void setColor(Color color) {
		this.color.set(color);
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}

}
