package de.instinct.engine.util;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;

public class VictoryCalculator {
	
	public static void checksVictory(GameState state) {
	    if (state.winner != 0) return;
	    checkSurrenderVictory(state);
	    checkMapATPVictory(state);
	    checkEliminationVictory(state);
	    if (state.gameTimeMS >= state.maxGameTimeMS) {
	    	calculatePlanetCountVictory(state);
	    	if (state.winner == 0) {
	    		calculateATPVictory(state);
	    	}
	    }
	}
	
	private static void checkSurrenderVictory(GameState state) {
		if (state.surrendered != 0) {
			state.winner = state.surrendered == 1 ? 2 : 1;
		}
	}

	private static void calculateATPVictory(GameState state) {
		int highestTeamId = 0;
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
    			if (EngineUtility.getPlayer(state.players, planet.ownerId).teamId == 1) {
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
	        if (EngineUtility.getPlayer(state.players, planet.ownerId).teamId == 1) {
	        	team1HasUnit = true;
	        }
	        if (EngineUtility.getPlayer(state.players, planet.ownerId).teamId == 2) {
	        	team2HasUnit = true;
	        }
	    }
	    for (Ship ship : state.ships) {
	        if (EngineUtility.getPlayer(state.players, ship.ownerId).teamId == 1) {
	        	team1HasUnit = true;
	        }
	        if (EngineUtility.getPlayer(state.players, ship.ownerId).teamId == 2) {
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
