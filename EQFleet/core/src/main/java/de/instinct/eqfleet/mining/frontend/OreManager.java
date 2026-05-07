package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.graphics.Color;

import de.instinct.engine.mining.entity.asteroid.ResourceType;

public class OreManager {
	
	private static Color carbonColor = new Color(0.15f, 0.15f, 0.15f, 1f);
	
	public static Color getColorForResourceType(ResourceType resourceType) {
		switch (resourceType) {
		case IRON:
			return Color.BROWN;
		case CARBON:
			return carbonColor;
		case COPPER:
			return Color.ORANGE;
		case ALUMINUM:
			return Color.LIGHT_GRAY;
		case TITANIUM:
			return Color.BLUE;
		case CHROMIUM:
			return Color.PINK;
		case NICKEL:
			return Color.OLIVE;
		case SILICON:
			return Color.CYAN;
		case LITHIUM:
			return Color.GREEN;
		case SULFUR:
			return Color.YELLOW;

		default:
			return Color.CLEAR;
		}
	}

}
