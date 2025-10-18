package de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;

import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class EQShape {
	
	private Matrix4 projectionMatrix;
	private EQGlowConfig glowConfig;
	private Color color;

}
