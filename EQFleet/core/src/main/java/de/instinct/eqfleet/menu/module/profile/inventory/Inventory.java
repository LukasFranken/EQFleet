package de.instinct.eqfleet.menu.module.profile.inventory;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.core.API;
import de.instinct.api.meta.dto.Resource;
import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.net.WebManager;

public class Inventory {
	
	public static void loadData() {
		WebManager.enqueue(
			    () -> API.meta().resources(API.authKey),
			    result -> {
			        InventoryModel.resources = result;
			    }
		);
	}
	
	public static long getResource(Resource type) {
		for (ResourceAmount amount : InventoryModel.resources.getResources()) {
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
