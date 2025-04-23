package de.instinct.eqfleetshared.gamelogic;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.eqfleetshared.gamelogic.event.model.GameEvent;
import de.instinct.eqfleetshared.gamelogic.event.model.subtypes.FleetMovementEvent;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.gamelogic.model.Planet;
import de.instinct.eqfleetshared.gamelogic.model.Player;
import de.instinct.eqfleetshared.gamelogic.order.OrderProcessor;
import de.instinct.eqfleetshared.gamelogic.order.OrderValidator;
import de.instinct.eqfleetshared.gamelogic.order.model.GameOrder;

public class EventEngine {
	
	private OrderValidator orderValidator = new OrderValidator();
	private OrderProcessor orderProcessor = new OrderProcessor();
	private Queue<GameOrder> unprocessedOrders = new ConcurrentLinkedQueue<>();
	
	public boolean update(GameState state, long progressionMS) {
	    long targetTime = state.gameTimeMS + progressionMS;

	    while (!state.activeEvents.isEmpty()) {
	        GameEvent nextEvent = state.activeEvents.peek();
	        if (nextEvent.eventFinalizationTimestamp() <= targetTime) {
	            state.activeEvents.poll();

	            long deltaToEvent = nextEvent.eventFinalizationTimestamp() - state.gameTimeMS;
	            if (deltaToEvent > 0) {
	                advanceTime(state, deltaToEvent);
	            }
	            
	            applyEvent(state, nextEvent);
	        } else {
	            break;
	        }
	    }

	    long remainingDelta = targetTime - state.gameTimeMS;
	    if (remainingDelta > 0) {
	        advanceTime(state, remainingDelta);
	    }

	    state.gameTimeMS = targetTime;
	    return workOnOrderQueue(state);
	}
	
	private void advanceTime(GameState state, long deltaMS) {
	    for (Planet planet : state.planets) {
	        if (planet.ownerId != 0) {
	            Player owner = EngineUtility.getPlayer(state, planet.ownerId);
	            if (planet.ancient) {
	            	double available = planet.value;
	            	double desired = ((double) deltaMS / 1000D);
	            	double actualGain = Math.min(available, desired);

	            	planet.value -= actualGain;
	            	owner.ancientTechnologyPoints += actualGain;
	            	if (owner.ancientTechnologyPoints > state.atpToWin) {
	            		owner.ancientTechnologyPoints = state.atpToWin;
	            	}

	            	if (planet.value <= 0) {
	            	    planet.ownerId = 0;
	            	    planet.value = 0;
	            	}
	            } else {
	                double resourceIncrease = calculateResourceGeneration(owner, deltaMS);
	                planet.value += resourceIncrease;
	            }
	        }
	    }

	    for (Player player : state.players) {
	        player.currentCommandPoints += ((double) deltaMS / 1000D) * player.commandPointsGenerationSpeed;
	        if (player.currentCommandPoints > player.maxCommandPoints) {
	            player.currentCommandPoints = player.maxCommandPoints;
	        }
	    }

	    state.gameTimeMS += deltaMS;
	}
	
	private boolean workOnOrderQueue(GameState state) {
		boolean containedValidOrder = false;
		while (!unprocessedOrders.isEmpty()) {
			GameOrder order = unprocessedOrders.poll();
			if (orderValidator.isValid(state, order)) {
				state.activeEvents.add(orderProcessor.processToEvent(state, order));
				containedValidOrder = true;
			}
		}
		return containedValidOrder;
	}
	
	private void applyEvent(GameState state, GameEvent event) {
	    if (event instanceof FleetMovementEvent) {
	        applyFleetMovement(state, (FleetMovementEvent) event);
	    }
	}

	private void applyFleetMovement(GameState state, FleetMovementEvent event) {
	    Planet toPlanet = EngineUtility.getPlanet(state, event.toPlanetId);

	    if (toPlanet.ownerId != event.playerId) {
	        toPlanet.value -= event.value;
	        if (toPlanet.value < 0) {
	            toPlanet.ownerId = event.playerId;
	            toPlanet.value *= -1;
	        }
	    } else {
	        toPlanet.value += event.value;
	    }
	}
	
	private double calculateResourceGeneration(Player player, long deltaMS) {
		return ((double)deltaMS / 1000D) * player.resourceGenerationSpeed;
	}
	
	public void queue(GameState state, GameOrder order) {
		unprocessedOrders.add(order);
	}

}
