package de.instinct.eqfleetshared.gamelogic.order;

import de.instinct.eqfleetshared.gamelogic.EngineUtility;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.gamelogic.model.Planet;
import de.instinct.eqfleetshared.gamelogic.model.Player;
import de.instinct.eqfleetshared.gamelogic.order.model.GameOrder;
import de.instinct.eqfleetshared.gamelogic.order.model.subtypes.FleetMovementOrder;

public class OrderValidator {
	
	public boolean isValid(GameState state, GameOrder order) {
		if (order instanceof FleetMovementOrder) {
			FleetMovementOrder fleetMovementOrder = (FleetMovementOrder)order;
			Planet fromPlanet = EngineUtility.getPlanet(state, fleetMovementOrder.fromPlanetId);
			Player player = EngineUtility.getPlayer(state, fleetMovementOrder.factionId);
			
			if (fromPlanet.ownerId != fleetMovementOrder.factionId) return false;
			if (fleetMovementOrder.fromPlanetId == fleetMovementOrder.toPlanetId) return false;
			if (fromPlanet.value < 2) return false;
			if (player.currentCommandPoints < 1) return false;
			
			return true;
		}
		return false;
	}

}
