package de.instinct.engine.util;

import java.util.List;

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

	    for (Player p : state.players) {
	        if (state.teamATPs.get(p.teamId) >= state.atpToWin) {
	            state.winner = p.teamId;
	            return;
	        }
	    }

	    if (state.gameTimeMS >= state.maxGameTimeMS) {
	    	int highestTeamId = 1;
		    double highestValue = 0;
	        for (int teamid : state.teamATPs.keySet()) {
				if (state.teamATPs.get(teamid) > highestValue) {
					highestValue = state.teamATPs.get(teamid);
					highestTeamId = teamid;
				}
	        }
	        state.winner = highestTeamId;
	    }
	}

}
