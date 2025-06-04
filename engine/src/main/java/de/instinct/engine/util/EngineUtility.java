package de.instinct.engine.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.planet.Planet;

public class EngineUtility {
	
	public static final Vector2 MAP_BOUNDS = new Vector2(1000, 2000);
	public static final float PLANET_RADIUS = 50f;
	
	public static Player getPlayer(List<Player> players, int playerId) {
		for (Player player : players) {
			if (player.id == playerId) {
				return player;
			}
		}
		throw new IllegalStateException("Player with playerId " + playerId + " not found in GameState." + players.size());
	}
	
	public static Planet getPlanet(List<Planet> planets, int planetId) {
		for (Planet planet : planets) {
			if (planet.id == planetId) {
				return planet;
			}
		}
		return null;
	}
	
	public static PlayerConnectionStatus getPlayerConnectionStatus(List<PlayerConnectionStatus> playerConnectionStatus, int playerId) {
		for (PlayerConnectionStatus connectionStatus : playerConnectionStatus) {
			if (connectionStatus.playerId == playerId) {
				return connectionStatus;
			}
		}
		return null;
	}
	
	public static void checkVictory(GameState state) {
	    if (state.winner != 0) return;

	    // build a map from playerId â†’ teamId
	    Map<Integer,Integer> playerToTeam = new HashMap<>();
	    for (Player p : state.players) {
	        playerToTeam.put(p.id, p.teamId);
	    }

	    // 1) ATP INSTANT WIN (and accumulate totals)
	    for (Player p : state.players) {
	        if (state.teamATPs.get(p.teamId) >= state.atpToWin) {
	            state.winner = p.teamId;
	            return;
	        }
	    }

	    // 2) TIMEOUT VICTORY WITH PLANET tiebreaker
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
	    

	    // build the set of teams
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
