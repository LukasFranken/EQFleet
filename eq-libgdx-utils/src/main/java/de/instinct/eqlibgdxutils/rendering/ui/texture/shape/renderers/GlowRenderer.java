package de.instinct.eqlibgdxutils.rendering.ui.texture.shape.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.GlobalRenderingConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;

public class GlowRenderer {
	
	private ExtendedShapeRenderer shapeRenderer;
	
	private float baseGlowFactor = 0.2f;
	
	public GlowRenderer(ExtendedShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
	}
	
	public void rectangle(Rectangle bounds, float thickness, EQGlowConfig glowConfig) {
		Color baseColor = new Color(shapeRenderer.getColor());
		int radius = glowConfig.getGlowRadius() > 0 ? glowConfig.getGlowRadius() : GlobalRenderingConfiguration.defaultGlowRadius;
		float strength = glowConfig.getGlowStrength() > 0 ? glowConfig.getGlowStrength() : GlobalRenderingConfiguration.defaultGlowStrength;
		for (int i = 0; i < radius; i++) {
			Color currentGlowColor = new Color(baseColor);
			currentGlowColor.a = baseColor.a * baseGlowFactor * strength * (1f - ((float)i / (float)radius));
			shapeRenderer.setColor(currentGlowColor);
			shapeRenderer.rect(new Rectangle(bounds.x - i, bounds.y - i, bounds.width + (i * 2), bounds.height + (i * 2)), 1f);
		}
		if (thickness > 0) {
			for (int i = 0; i < radius; i++) {
				Color currentGlowColor = new Color(baseColor);
				currentGlowColor.a = baseColor.a * baseGlowFactor * strength * (1f - ((float)i / (float)radius));
				shapeRenderer.setColor(currentGlowColor);
				shapeRenderer.rect(new Rectangle(bounds.x + thickness + i, bounds.y + thickness + i, bounds.width - (i * 2) - (thickness * 2), bounds.height - (i * 2) - (thickness * 2)), 1f);
			}
		}
		shapeRenderer.setColor(baseColor);
	}

}
