package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.graphics.Color;

import de.instinct.engine.mining.entity.asteroid.ResourceType;

public class OreManager {
	
	private static Color carbonColor = new Color(0.15f, 0.15f, 0.15f, 1f);
	private static Color equilibriumColor = new Color(0.5f, 0f, 0f, 1f);
	
	public static Color getColorForResourceType(ResourceType resourceType) {
		switch (resourceType) {
		case IRON:
			return Color.BROWN;
		case CARBON:
			return carbonColor;
		case SILICON:
			return Color.CYAN;
		case GOLD:
			return Color.YELLOW;
		case URANIUM:
			return Color.GREEN;
		case ALUMINUM:
			return Color.LIGHT_GRAY;
		case THORIUM:
			return Color.BLUE;
		case XENON:
			return Color.PURPLE;
		case SELENIUM:
			return Color.PINK;
		case EQUILIBRIUM:
			return equilibriumColor;

		default:
			return Color.CLEAR;
		}
	}

}
