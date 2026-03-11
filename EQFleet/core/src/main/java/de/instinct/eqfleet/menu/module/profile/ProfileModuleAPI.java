package de.instinct.eqfleet.menu.module.profile;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.meta.dto.Resource;
import de.instinct.api.meta.dto.ResourceAmount;

public class ProfileModuleAPI {
	
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
			return Color.GREEN;
		case METAL:
			return Color.BROWN;
		case CRYSTAL:
			return Color.CYAN;
		case DEUTERIUM:
			return Color.PURPLE;
		case EQUILIBRIUM:
			return new Color(0.7f, 0f, 0f, 1.0f);
		}
		return Color.WHITE;
	}
	
	public static boolean canAfford(ResourceAmount cost) {
		return getResource(cost.getType()) >= Math.abs(cost.getAmount());
	}

}
