package de.instinct.engine;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.event.GameEvent;
import de.instinct.engine.model.event.types.FleetMovementEvent;

public class EngineUtility {
	
	public static final Vector2 MAP_BOUNDS = new Vector2(1000, 2000);
	public static final float PLANET_RADIUS = 50f;
	
	public static Player getPlayer(GameState state, int factionId) {
		for (Player player : state.players) {
			if (player.factionId == factionId) {
				return player;
			}
		}
		throw new IllegalStateException("Player with factionId " + factionId + " not found in GameState." + state.players.size());
	}
	
	public static Player getPlayer(GameState state, String uuid) {
		for (Player player : state.players) {
			if (player.uuid.contentEquals(uuid)) {
				return player;
			}
		}
		return null;
	}
	
	public static Planet getPlanet(GameState state, int planetId) {
		for (Planet planet : state.planets) {
			if (planet.id == planetId) {
				return planet;
			}
		}
		return null;
	}
	
	public static long calculateTotalTravelTimeMS(GameState state, FleetMovementEvent fleetMovement) {
		Planet fromPlanet = getPlanet(state, fleetMovement.fromPlanetId);
		Planet toPlanet = getPlanet(state, fleetMovement.toPlanetId);
		return (long)(Vector3.dst(fromPlanet.xPos, fromPlanet.yPos, 0, toPlanet.xPos, toPlanet.yPos, 0) / getPlayer(state, fleetMovement.playerId).fleetMovementSpeed) * 1000;
	}
	
	public static Vector3 getFleetWorldPosition(GameState state, FleetMovementEvent movement) {
		Planet from = EngineUtility.getPlanet(state, movement.fromPlanetId);
		Planet to = EngineUtility.getPlanet(state, movement.toPlanetId);

		float dx = to.xPos - from.xPos;
		float dy = to.yPos - from.yPos;
		float distance = (float) Math.sqrt(dx * dx + dy * dy);

		float dirX = dx / distance;
		float dirY = dy / distance;

		float startX = from.xPos + dirX * EngineUtility.PLANET_RADIUS;
		float startY = from.yPos + dirY * EngineUtility.PLANET_RADIUS;

		float endX = to.xPos - dirX * EngineUtility.PLANET_RADIUS;
		float endY = to.yPos - dirY * EngineUtility.PLANET_RADIUS;

		float progress = (float) (state.gameTimeMS - movement.startGameTimeMS) / (float) movement.durationMS;
		float worldX = startX + (endX - startX) * progress;
		float worldY = startY + (endY - startY) * progress;

		return new Vector3(worldX, worldY, 0f);
	}
	
	public static void checkVictory(GameState state) {
	    if (state.winner != 0) return;

	    // ATP INSTANT WIN
	    for (Player player : state.players) {
	        if (player.ancientTechnologyPoints >= state.atpToWin) {
	            state.winner = (int) player.factionId;
	            return;
	        }
	    }

	    // TIMEOUT VICTORY
	    if (state.gameTimeMS >= state.maxGameTimeMS) {
	        Player best = null;
	        for (Player player : state.players) {
	            if (best == null || player.ancientTechnologyPoints > best.ancientTechnologyPoints) {
	                best = player;
	            }
	        }
	        if (best != null) {
	            state.winner = (int) best.factionId;
	            return;
	        } else {
	        	state.winner = 1;
	        }
	    }

	    // DEFAULT PLANET+FLEET ELIMINATION
	    int player1Planets = 0, player2Planets = 0;
	    int player1Fleets = 0, player2Fleets = 0;

	    for (Planet planet : state.planets) {
	        if (planet.ownerId == 1) player1Planets++;
	        else if (planet.ownerId == 2) player2Planets++;
	    }

	    for (GameEvent event : state.activeEvents) {
	        if (event instanceof FleetMovementEvent) {
	            FleetMovementEvent fleet = (FleetMovementEvent) event;
	            if (fleet.playerId == 1) player1Fleets++;
	            else if (fleet.playerId == 2) player2Fleets++;
	        }
	    }

	    if (player2Planets == 0 && player2Fleets == 0) {
	        state.winner = 1;
	    } else if (player1Planets == 0 && player1Fleets == 0) {
	        state.winner = 2;
	    }
	}

}
