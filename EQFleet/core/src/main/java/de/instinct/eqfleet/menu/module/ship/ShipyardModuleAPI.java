package de.instinct.eqfleet.menu.module.ship;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.shipyard.dto.ship.PlayerShipData;
import de.instinct.api.shipyard.dto.ship.component.ShipComponentType;

public class ShipyardModuleAPI {
	
	public static boolean hasActiveShip() {
		boolean hasActiveShip = false;
		for (PlayerShipData ship : ShipyardModel.playerShipyard.getShips()) {
			if (ship.isInUse()) {
				hasActiveShip = true;
				break;
			}
		}
		return hasActiveShip;
	}
	
	public static Color getPartTypeColor(ShipComponentType type) {
		switch (type) {
			case CORE:
				return Color.PURPLE;
			case ENGINE:
				return Color.YELLOW;
			case HULL:
				return Color.ORANGE;
			case SHIELD:
				return Color.CYAN;
			case WEAPON:
				return Color.RED;
		}
		return Color.GRAY;
	}

}
