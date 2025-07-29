package de.instinct.engine.util;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.Ship;
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
	    checkMapATPVictory(state);
	    checkEliminationVictory(state);
	    if (state.gameTimeMS >= state.maxGameTimeMS) {
	    	calculatePlanetCountVictory(state);
	    	if (state.winner == 0) {
	    		calculateATPVictory(state);
	    	}
	    }
	}
	
	private static void calculateATPVictory(GameState state) {
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

	private static void calculatePlanetCountVictory(GameState state) {
		int team1Planets = 0;
    	int team2Planets = 0;
    	for (Planet planet : state.planets) {
    		if (planet.ownerId != 0) {
    			if (getPlayer(state.players, planet.ownerId).teamId == 1) {
    				team1Planets++;
    			} else {
    				team2Planets++;
    			}
    		}
    	}
    	if (team1Planets > team2Planets) {
    		state.winner = 1;
    	} 
    	if (team1Planets < team2Planets) {
    		state.winner = 2;
    	}
	}

	private static void checkMapATPVictory(GameState state) {
		for (Player player : state.players) {
	        if (state.teamATPs.get(player.teamId) >= state.atpToWin) {
	            state.winner = player.teamId;
	            return;
	        }
	    }
	}

	private static void checkEliminationVictory(GameState state) {
		boolean team1HasUnit = false;
	    boolean team2HasUnit = false;
	    for (Planet planet : state.planets) {
	        if (getPlayer(state.players, planet.ownerId).teamId == 1) {
	        	team1HasUnit = true;
	        }
	        if (getPlayer(state.players, planet.ownerId).teamId == 2) {
	        	team2HasUnit = true;
	        }
	    }
	    for (Ship ship : state.ships) {
	        if (getPlayer(state.players, ship.ownerId).teamId == 1) {
	        	team1HasUnit = true;
	        }
	        if (getPlayer(state.players, ship.ownerId).teamId == 2) {
	        	team2HasUnit = true;
	        }
	    }
	    if (!team1HasUnit) {
	    	state.winner = 2;
	    }
		if (!team2HasUnit) {
			state.winner = 1;
		}
	}

}
