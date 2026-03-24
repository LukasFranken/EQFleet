package de.instinct.engine.planet;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.TurretProcessor;
import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.player.PlayerProcessor;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.util.EngineUtility;

public class PlanetProcessor {
	
	public static void initialize(GameState state) {
		for (Planet planet : state.entityData.planets) {
			Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
			transferPlanetControl(state, planet, owner);
		}
	}
	
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
	
	public static Planet createPlanet(GameState state, Player owner, Vector2 position, boolean ancient) {
		Planet planet = new Planet();
		EntityProcessor.initializeEntity(planet, state);
		planet.radius = EngineUtility.PLANET_RADIUS;
		planet.position = position;
		planet.ancient = ancient;
		planet.ownerId = owner.id;
		return planet;
	}

	public static void transferPlanetControl(GameState state, Planet planet, Player newOwner) {
		planet.ownerId = newOwner.id;
		planet.resourceGenerationSpeed = newOwner.planetData.baseResourceGenerationSpeed;
		planet.turretSlotsLeft = newOwner.planetData.turretSlots;
		if (newOwner.turrets.size() > 0) {
			TurretProcessor.createTurret(planet, 0, state, false);
		}
	}

}
