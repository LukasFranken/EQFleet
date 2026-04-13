package de.instinct.engine.fleet.entity.planet;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.unit.UnitProcessor;
import de.instinct.engine.fleet.entity.unit.turret.Turret;
import de.instinct.engine.fleet.entity.unit.turret.TurretProcessor;
import de.instinct.engine.fleet.player.FleetPlayer;
import de.instinct.engine.fleet.player.FleetPlayerProcessor;
import de.instinct.engine.fleet.stats.StatCollector;
import de.instinct.engine.fleet.stats.model.PlayerStatistic;

public class PlanetProcessor {
	
	public static final Vector2 MAP_BOUNDS = new Vector2(1000, 2000);
	public static final float PLANET_RADIUS = 50f;
	
	private UnitProcessor unitProcessor;
	private FleetPlayerProcessor playerProcessor;
	private TurretProcessor turretProcessor;
	
	public PlanetProcessor() {
		unitProcessor = new UnitProcessor();
		playerProcessor = new FleetPlayerProcessor();
		turretProcessor = new TurretProcessor();
	}
	
	public void initialize(FleetGameState state) {
		for (Planet planet : state.entityData.planets) {
			FleetPlayer owner = playerProcessor.getFleetPlayer(state.playerData.players, planet.ownerId);
			transferPlanetControl(state, planet, owner);
		}
	}
	
	public void update(FleetGameState state, long deltaMS) {
		for (Planet planet : state.entityData.planets) {
	        if (planet.ownerId != 0) {
	            FleetPlayer owner = playerProcessor.getFleetPlayer(state.playerData.players, planet.ownerId);
	            if (planet.ancient) {
	            	double available = owner.currentResources;
	            	double desired = ((double) deltaMS / 1000D) * state.staticData.ancientPlanetResourceDegradationFactor;
	            	double actualLoss = Math.min(available, desired);
	            	playerProcessor.addResources(owner, -actualLoss);
	            	
	            	double atpGain = actualLoss / state.staticData.ancientPlanetResourceDegradationFactor;
	            	double newATPValue = state.teamATPs.get(owner.teamId) + atpGain;
	            	state.teamATPs.put(owner.teamId, newATPValue > state.staticData.atpToWin ? state.staticData.atpToWin : newATPValue);

	            	if (available < desired) {
	            	    planet.ownerId = 0;
	            	    owner.currentResources = 0;
	            	    Turret ancientTurret = turretProcessor.getPlanetTurret(state.entityData.turrets, planet.id);
	            	    if (ancientTurret != null) {
	            	    	ancientTurret.flaggedForDestroy = true;
	            	    }
	            	}
	            	
	            	PlayerStatistic playerStat = StatCollector.getPlayer(state.gameUUID, owner.id);
	            	playerStat.setAtpGained(playerStat.getAtpGained() + atpGain);
	            } else {
	            	playerProcessor.addResources(owner, calculateResourceGeneration(planet, deltaMS));
	            }
	        }
	    }
	}
	
	private double calculateResourceGeneration(Planet planet, long deltaMS) {
		return ((double)deltaMS / 1000D) * planet.resourceGenerationSpeed;
	}
	
	public Planet createPlanet(FleetGameState state, Player owner, Vector2 position, boolean ancient) {
		Planet planet = new Planet();
		unitProcessor.initializeEntity(planet, state);
		planet.radius = PLANET_RADIUS;
		planet.position = position;
		planet.ancient = ancient;
		planet.ownerId = owner.id;
		return planet;
	}

	public void transferPlanetControl(FleetGameState state, Planet planet, FleetPlayer newOwner) {
		planet.ownerId = newOwner.id;
		planet.resourceGenerationSpeed = newOwner.planetData.baseResourceGenerationSpeed;
		planet.turretSlotsLeft = newOwner.planetData.turretSlots;
		if (newOwner.turrets.size() > 0) {
			turretProcessor.createTurret(planet, 0, state, false);
		}
	}
	
	public Planet getPlanet(List<Planet> planets, int planetId) {
		for (Planet planet : planets) {
			if (planet.id == planetId) {
				return planet;
			}
		}
		return null;
	}
	
	public boolean mapHasAncient(List<Planet> planets) {
		for (Planet planet : planets) {
			if (planet.ancient) {
				return true;
			}
		}
		return false;
	}

}
