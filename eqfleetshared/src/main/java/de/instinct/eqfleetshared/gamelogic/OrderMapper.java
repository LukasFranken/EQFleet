package de.instinct.eqfleetshared.gamelogic;

import de.instinct.eqfleetshared.gamelogic.order.model.subtypes.FleetMovementOrder;
import de.instinct.eqfleetshared.net.message.types.FleetMovementMessage;

public class OrderMapper {
	
	public FleetMovementOrder map(FleetMovementMessage fleetMovementMessage) {
		FleetMovementOrder fleetMovementOrder = new FleetMovementOrder();
		fleetMovementOrder.playerId = fleetMovementMessage.playerId;
		fleetMovementOrder.fromPlanetId = fleetMovementMessage.fromPlanetId;
		fleetMovementOrder.toPlanetId = fleetMovementMessage.toPlanetId;
		return fleetMovementOrder;
	}

}
