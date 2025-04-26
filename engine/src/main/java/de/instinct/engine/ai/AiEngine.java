package de.instinct.engine.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.event.GameEvent;
import de.instinct.engine.model.event.types.FleetMovementEvent;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.FleetMovementOrder;

public class AiEngine {
	
	public AiPlayer initialize(AiDifficulty difficulty) {
		AiPlayer newAiPlayer = new AiPlayer();
		newAiPlayer.difficulty = difficulty;
		
		newAiPlayer.uuid = UUID.randomUUID().toString();
		newAiPlayer.name = "AI (" + difficulty.toString() + ")";
		newAiPlayer.fleetMovementSpeed = 50f;
		newAiPlayer.resourceGenerationSpeed = 1f;
		if (difficulty == AiDifficulty.RETARDED) {
			newAiPlayer.resourceGenerationSpeed = 0.8f;
		}
		newAiPlayer.commandPointsGenerationSpeed = 0.1f;
		newAiPlayer.startCommandPoints = 3;
		newAiPlayer.maxCommandPoints = 10;
		return newAiPlayer;
	}
	
	public List<GameOrder> act(AiPlayer aiPlayer, GameState state) {
		List<GameOrder> orders = new ArrayList<>();
		if (aiPlayer.currentCommandPoints > 0) {
			List<Planet> ownPlanets = new ArrayList<>();
			for (Planet planet : state.planets) {
				if (planet.ownerId == aiPlayer.factionId) {
					ownPlanets.add(planet);
				}
			}
			List<FleetMovementEvent> capturePlanetTargets = new ArrayList<>();
			for (Planet planet : state.planets) {
				if (planet.ownerId != aiPlayer.factionId && !planet.ancient) {
					for (Planet ownPlanet : ownPlanets) {
						FleetMovementEvent capturePlanetTarget = new FleetMovementEvent();
						capturePlanetTarget.playerId = aiPlayer.factionId;
						capturePlanetTarget.fromPlanetId = ownPlanet.id;
						capturePlanetTarget.toPlanetId = planet.id;
						if (planet.ownerId == 0) {
							if (planet.value < (int)(ownPlanet.value / 2)) {
								capturePlanetTargets.add(capturePlanetTarget);
							}
						} else {
							double enemyPlanetValueAtArrival = planet.value + ((EngineUtility.calculateTotalTravelTimeMS(state, capturePlanetTarget) / 1000D) * EngineUtility.getPlayer(state, planet.ownerId).resourceGenerationSpeed);
							if (enemyPlanetValueAtArrival < (int)(ownPlanet.value / 2)) {
								capturePlanetTargets.add(capturePlanetTarget);
							}
						}
					}
				}
			}
			FleetMovementEvent lowestUnorderedPlanetTarget = null;
			for (FleetMovementEvent fleetMovementEvent : capturePlanetTargets) {
				boolean orderToPlanetExists = false;
				for (GameEvent event : state.activeEvents) {
					if (event instanceof FleetMovementEvent) {
						if (((FleetMovementEvent)event).playerId == aiPlayer.factionId) {
							if (fleetMovementEvent.toPlanetId == ((FleetMovementEvent)event).toPlanetId) {
								orderToPlanetExists = true;
							}
						}
					}
				}
					
				if (!orderToPlanetExists) {
					if (lowestUnorderedPlanetTarget == null) {
						lowestUnorderedPlanetTarget = fleetMovementEvent;
					} else {
						if (EngineUtility.calculateTotalTravelTimeMS(state, fleetMovementEvent) < EngineUtility.calculateTotalTravelTimeMS(state, lowestUnorderedPlanetTarget)) {
							lowestUnorderedPlanetTarget = fleetMovementEvent;
						}
					}
				}
			}
			if (lowestUnorderedPlanetTarget != null) {
				FleetMovementOrder newCaptureOrder = new FleetMovementOrder();
				newCaptureOrder.factionId = aiPlayer.factionId;
				newCaptureOrder.fromPlanetId = lowestUnorderedPlanetTarget.fromPlanetId;
				newCaptureOrder.toPlanetId = lowestUnorderedPlanetTarget.toPlanetId;
				orders.add(newCaptureOrder);
			}
		}
		return orders;
	}

}
