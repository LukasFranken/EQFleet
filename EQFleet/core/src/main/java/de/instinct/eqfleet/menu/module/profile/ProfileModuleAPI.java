package de.instinct.eqfleet.menu.module.profile;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.meta.dto.Resource;
import de.instinct.api.meta.dto.ResourceAmount;

public class ProfileModuleAPI {
	
	private static final Color CREDITS_COLOR = new Color(0f, 0.5f, 0f, 1f);
	private static final Color METAL_COLOR = Color.BROWN;
	private static final Color CRYSTAL_COLOR = Color.CYAN;
	private static final Color DEUTERIUM_COLOR = Color.PURPLE;
	private static final Color EQUILIBRIUM_COLOR = new Color(0.7f, 0f, 0f, 1.0f);
	
	public static long getResource(Resource type) {
		for (ResourceAmount amount : ProfileModel.resources.getResources()) {
			if (amount.getType() == type) {
				return amount.getAmount();
			}
		}
		return 0;
	}
	
	public static Color getColorForResource(Resource type) {
		switch (type) {
		case CREDITS:
			return CREDITS_COLOR;
		case METAL:
			return METAL_COLOR;
		case CRYSTAL:
			return CRYSTAL_COLOR;
		case DEUTERIUM:
			return DEUTERIUM_COLOR;
		case EQUILIBRIUM:
			return EQUILIBRIUM_COLOR;
		}
		return Color.WHITE;
	}
	
	public static boolean canAfford(ResourceAmount cost) {
		return getResource(cost.getType()) >= Math.abs(cost.getAmount());
	}

}
