package de.instinct.engine.order;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.order.types.FleetMovementOrder;

public class OrderValidator {
	
	public boolean isValid(GameState state, GameOrder order) {
		if (order instanceof FleetMovementOrder) {
			FleetMovementOrder fleetMovementOrder = (FleetMovementOrder)order;
			Planet fromPlanet = EngineUtility.getPlanet(state, fleetMovementOrder.fromPlanetId);
			Player player = EngineUtility.getPlayer(state, fleetMovementOrder.playerId);
			
			if (fromPlanet.ownerId != fleetMovementOrder.playerId) return false;
			if (fleetMovementOrder.fromPlanetId == fleetMovementOrder.toPlanetId) return false;
			if (fromPlanet.value < 2) return false;
			if (player.currentCommandPoints < 1) return false;
			return true;
		}
		return false;
	}

}
