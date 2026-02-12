package de.instinct.engine.planet;

import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.planet.PlanetData;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.util.EngineUtility;

public class PlanetProcessor extends EntityProcessor {
	
	public void update(GameState state, long deltaMS) {
		for (Planet planet : state.entityData.planets) {
	        if (planet.ownerId != 0) {
	            Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
	            if (planet.ancient) {
	            	double available = planet.currentResources;
	            	double desired = ((double) deltaMS / 1000D) * state.staticData.ancientPlanetResourceDegradationFactor;
	            	double actualGain = Math.min(available, desired);

	            	planet.currentResources -= actualGain;
	            	double newATPValue = state.teamATPs.get(owner.teamId) + (actualGain / state.staticData.ancientPlanetResourceDegradationFactor);
	            	state.teamATPs.put(owner.teamId, newATPValue > state.staticData.atpToWin ? state.staticData.atpToWin : newATPValue);

	            	if (planet.currentResources <= 0) {
	            	    planet.ownerId = 0;
	            	    planet.currentResources = 0;
	            	}
	            	
	            	PlayerStatistic playerStat = StatCollector.getPlayer(state.gameUUID, owner.id);
	            	playerStat.setAtpGained(playerStat.getAtpGained() + (actualGain / state.staticData.ancientPlanetResourceDegradationFactor));
	            } else {
	                double resourceIncrease = calculateResourceGeneration(owner, deltaMS);
	                planet.currentResources += resourceIncrease;
	                if (planet.currentResources > owner.planetData.maxResourceCapacity) {
	                	planet.currentResources = owner.planetData.maxResourceCapacity;
	                }
	            }
	        }
	    }
	}
	
	private double calculateResourceGeneration(Player player, long deltaMS) {
		return ((double)deltaMS / 1000D) * player.planetData.resourceGenerationSpeed;
	}
	
	public Planet createPlanet(PlanetData planetData, GameState state) {
		Planet planet = new Planet();
		super.initializeEntity(planet, state);
		planet.resourceGenerationSpeed = planetData.resourceGenerationSpeed;
		planet.maxResourceCapacity = planetData.maxResourceCapacity;
		planet.radius = EngineUtility.PLANET_RADIUS;
		return planet;
	}

}
