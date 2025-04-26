package de.instinct.engine.order;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.event.GameEvent;
import de.instinct.engine.model.event.types.FleetMovementEvent;
import de.instinct.engine.order.types.FleetMovementOrder;

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
