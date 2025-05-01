package de.instinct.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	public static Player getPlayer(GameState state, int playerId) {
		for (Player player : state.players) {
			if (player.playerId == playerId) {
				return player;
			}
		}
		throw new IllegalStateException("Player with playerId " + playerId + " not found in GameState." + state.players.size());
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

	    // build a map from playerId → teamId
	    Map<Integer,Integer> playerToTeam = new HashMap<>();
	    for (Player p : state.players) {
	        playerToTeam.put(p.playerId, p.teamId);
	    }

	    // 1) ATP INSTANT WIN (and accumulate totals)
	    for (Player p : state.players) {
	        if (state.teamATPs.get(p.teamId) >= state.atpToWin) {
	            state.winner = p.teamId;
	            return;
	        }
	    }

	    // 2) TIMEOUT VICTORY WITH PLANET TIE‐BREAKER
	    if (state.gameTimeMS >= state.maxGameTimeMS) {
	        // find the maximum ATP value
	        double maxATP = state.teamATPs.values().stream()
	            .mapToDouble(Double::doubleValue)
	            .max()
	            .orElse(0.0);

	        // collect all teams that share that max
	        List<Integer> topTeams = state.teamATPs.entrySet().stream()
	            .filter(e -> e.getValue() == maxATP)
	            .map(Map.Entry::getKey)
	            .collect(Collectors.toList());

	        if (topTeams.size() == 1) {
	            // clear winner by ATP
	            state.winner = topTeams.get(0);
	            return;
	        }

	        // tie: break by planet count
	        Map<Integer, Integer> teamPlanets = new HashMap<>();
	        for (Planet pl : state.planets) {
	            int teamId = playerToTeam.getOrDefault(pl.ownerId, 0);
	            if (teamId != 0) {
	                teamPlanets.merge(teamId, 1, Integer::sum);
	            }
	        }
	        // pick the tied team with the most planets
	        int winningTeam = topTeams.stream()
	            .max(Comparator.comparingInt(t -> teamPlanets.getOrDefault(t, 0)))
	            .orElse(topTeams.get(0));

	        state.winner = winningTeam;
	        return;
	    }

	    // 3) DEFAULT PLANET+FLEET ELIMINATION
	    //     eliminate any team with no planets AND no fleets
	    Map<Integer, Integer> teamPlanets = new HashMap<>();
	    for (Planet pl : state.planets) {
	        int teamId = playerToTeam.getOrDefault(pl.ownerId, 0);
	        if (teamId != 0) {
	            teamPlanets.merge(teamId, 1, Integer::sum);
	        }
	    }

	    Map<Integer, Integer> teamFleets = new HashMap<>();
	    for (GameEvent evt : state.activeEvents) {
	        if (evt instanceof FleetMovementEvent) {
	            FleetMovementEvent fm = (FleetMovementEvent) evt;
	            int teamId = playerToTeam.getOrDefault(fm.playerId, 0);
	            if (teamId != 0) {
	                teamFleets.merge(teamId, 1, Integer::sum);
	            }
	        }
	    }

	    // build the set of “alive” teams
	    Set<Integer> alive = new HashSet<>(state.teamATPs.keySet());
	    for (Integer teamId : new ArrayList<>(alive)) {
	        int planets = teamPlanets.getOrDefault(teamId, 0);
	        int fleets  = teamFleets.getOrDefault(teamId, 0);
	        if (planets == 0 && fleets == 0) {
	            alive.remove(teamId);
	        }
	    }

	    if (alive.size() == 1) {
	        state.winner = alive.iterator().next();
	    }
	}

}
