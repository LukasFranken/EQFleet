package de.instinct.eqfleetshared.gamelogic.order;

import de.instinct.eqfleetshared.gamelogic.EngineUtility;
import de.instinct.eqfleetshared.gamelogic.event.model.GameEvent;
import de.instinct.eqfleetshared.gamelogic.event.model.subtypes.FleetMovementEvent;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.gamelogic.model.Planet;
import de.instinct.eqfleetshared.gamelogic.model.Player;
import de.instinct.eqfleetshared.gamelogic.order.model.GameOrder;
import de.instinct.eqfleetshared.gamelogic.order.model.subtypes.FleetMovementOrder;

public class OrderProcessor {

	public GameEvent processToEvent(GameState state, GameOrder order) {
		if (order instanceof FleetMovementOrder) {
			FleetMovementOrder fleetMovementOrder = (FleetMovementOrder)order;
			Planet fromPlanet = EngineUtility.getPlanet(state, fleetMovementOrder.fromPlanetId);
			Player player = EngineUtility.getPlayer(state, fleetMovementOrder.factionId);
			player.currentCommandPoints -= 1;
			FleetMovementEvent newFleetMovementEvent = new FleetMovementEvent();
			newFleetMovementEvent.playerId = fleetMovementOrder.factionId;
			newFleetMovementEvent.fromPlanetId = fleetMovementOrder.fromPlanetId;
			newFleetMovementEvent.toPlanetId = fleetMovementOrder.toPlanetId;
			newFleetMovementEvent.value = (int)(fromPlanet.value / 2);
			newFleetMovementEvent.durationMS = EngineUtility.calculateTotalTravelTimeMS(state, newFleetMovementEvent);
			setGeneralEventValues(state, newFleetMovementEvent);
			fromPlanet.value -= newFleetMovementEvent.value;
			return newFleetMovementEvent;
		}
		return null;
	}

	private void setGeneralEventValues(GameState state, GameEvent event) {
		event.startGameTimeMS = state.gameTimeMS;
	}

}
