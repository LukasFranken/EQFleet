package de.instinct.engine.planet;

import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.planet.PlanetData;
import de.instinct.engine.player.PlayerProcessor;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.util.EngineUtility;

public class PlanetProcessor {
	
	public static void update(GameState state, long deltaMS) {
		for (Planet planet : state.entityData.planets) {
	        if (planet.ownerId != 0) {
	            Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
	            if (planet.ancient) {
	            	double available = owner.currentResources;
	            	double desired = ((double) deltaMS / 1000D) * state.staticData.ancientPlanetResourceDegradationFactor;
	            	double actualLoss = Math.min(available, desired);

	            	double atpGain = actualLoss / state.staticData.ancientPlanetResourceDegradationFactor;
	            	double newATPValue = state.teamATPs.get(owner.teamId) + atpGain;
	            	state.teamATPs.put(owner.teamId, newATPValue > state.staticData.atpToWin ? state.staticData.atpToWin : newATPValue);

	            	if (available < desired) {
	            	    planet.ownerId = 0;
	            	    owner.currentResources = 0;
	            	}
	            	
	            	PlayerStatistic playerStat = StatCollector.getPlayer(state.gameUUID, owner.id);
	            	playerStat.setAtpGained(playerStat.getAtpGained() + atpGain);
	            } else {
	                PlayerProcessor.addResources(owner, calculateResourceGeneration(planet, deltaMS));
	            }
	        }
	    }
	}
	
	private static double calculateResourceGeneration(Planet planet, long deltaMS) {
		return ((double)deltaMS / 1000D) * planet.resourceGenerationSpeed;
	}
	
	public static Planet createPlanet(PlanetData planetData, GameState state) {
		Planet planet = new Planet();
		EntityProcessor.initializeEntity(planet, state);
		planet.resourceGenerationSpeed = planetData.baseResourceGenerationSpeed;
		planet.radius = EngineUtility.PLANET_RADIUS;
		return planet;
	}

}
