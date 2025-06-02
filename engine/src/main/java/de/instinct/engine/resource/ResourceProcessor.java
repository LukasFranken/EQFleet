package de.instinct.engine.resource;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;

public class ResourceProcessor {
	
	public void update(GameState state, long deltaMS) {
		for (Planet planet : state.planets) {
	        if (planet.ownerId != 0) {
	            Player owner = EngineUtility.getPlayer(state.players, planet.ownerId);
	            if (planet.ancient) {
	            	double available = planet.currentResources;
	            	double desired = ((double) deltaMS / 1000D) * state.ancientPlanetResourceDegradationFactor;
	            	double actualGain = Math.min(available, desired);

	            	planet.currentResources -= actualGain;
	            	double newATPValue = state.teamATPs.get(owner.teamId) + (actualGain / state.ancientPlanetResourceDegradationFactor);
	            	state.teamATPs.put(owner.teamId, newATPValue > state.atpToWin ? state.atpToWin : newATPValue);

	            	if (planet.currentResources <= 0) {
	            		planet.currentShield = 0;
	            		planet.currentArmor = 0;
	    				planet.weapon = null;
	    				planet.defense = null;
	            	    planet.ownerId = 0;
	            	    planet.currentResources = 0;
	            	}
	            } else {
	                double resourceIncrease = calculateResourceGeneration(owner, deltaMS);
	                planet.currentResources += resourceIncrease;
	                if (planet.currentResources > owner.planetData.maxResourceCapacity) {
	                	planet.currentResources = owner.planetData.maxResourceCapacity;
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
	}
	
	private double calculateResourceGeneration(Player player, long deltaMS) {
		return ((double)deltaMS / 1000D) * player.planetData.resourceGenerationSpeed;
	}

}
