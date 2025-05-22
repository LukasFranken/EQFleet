package de.instinct.engine;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.event.GameEvent;
import de.instinct.engine.model.event.types.FleetMovementEvent;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.OrderProcessor;
import de.instinct.engine.order.OrderValidator;

public class EventEngine {
	
	private OrderValidator orderValidator = new OrderValidator();
	private OrderProcessor orderProcessor = new OrderProcessor();
	private Queue<GameOrder> unprocessedOrders = new ConcurrentLinkedQueue<>();
	
	public boolean update(GameState state, long progressionMS) {
		if (state.started) {
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
		    
		    boolean newOrder = workOnOrderQueue(state);
		    return newOrder;
		}
		return false;
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
	            	double newATPValue = state.teamATPs.get(owner.teamId) + actualGain;
	            	state.teamATPs.put(owner.teamId, newATPValue > state.atpToWin ? state.atpToWin : newATPValue);

	            	if (planet.value <= 0) {
	            	    planet.ownerId = 0;
	            	    planet.value = 0;
	            	}
	            } else {
	                double resourceIncrease = calculateResourceGeneration(owner, deltaMS);
	                planet.value += resourceIncrease;
	                if (planet.value > owner.maxPlanetCapacity) {
	                	planet.value = owner.maxPlanetCapacity;
	                }
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
	    Player fromPlayer = EngineUtility.getPlayer(state, event.playerId);
	    Player toPlayer = EngineUtility.getPlayer(state, toPlanet.ownerId);
	    if (fromPlayer.teamId != toPlayer.teamId) {
	        toPlanet.value -= event.shipData.power;
	        if (toPlanet.value < 0) {
	            toPlanet.ownerId = event.playerId;
	            toPlanet.value *= -1;
	        }
	    } else {
	        toPlanet.value += event.shipData.cost;
	    }
	}
	
	private double calculateResourceGeneration(Player player, long deltaMS) {
		return ((double)deltaMS / 1000D) * player.resourceGenerationSpeed;
	}
	
	public void queue(GameState state, GameOrder order) {
		unprocessedOrders.add(order);
	}

}
