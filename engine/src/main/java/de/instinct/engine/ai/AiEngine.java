package de.instinct.engine.ai;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.ai.difficulty.AiStatManager;
import de.instinct.engine.ai.difficulty.DifficultyLoader;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.planet.PlanetData;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.components.CoreData;
import de.instinct.engine.model.ship.components.EngineData;
import de.instinct.engine.model.ship.components.types.CoreType;
import de.instinct.engine.model.ship.components.types.EngineType;
import de.instinct.engine.model.turret.PlatformData;
import de.instinct.engine.model.turret.PlatformType;
import de.instinct.engine.model.turret.TurretData;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.BuildTurretOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.util.EngineUtility;

public class AiEngine {
	
	private DifficultyLoader difficultyLoader;
	
	public AiEngine() {
		difficultyLoader = new DifficultyLoader();
	}
	
	public AiPlayer initialize(int threatLevel) {
		AiPlayer newAiPlayer = new AiPlayer();
		newAiPlayer.ships = new ArrayList<>();
		newAiPlayer.ships.add(createAIShip(threatLevel));
		
		newAiPlayer.turrets = new ArrayList<>();
		if (threatLevel >= 10) {
			newAiPlayer.turrets.add(createAITurret(threatLevel));
		}
		
		PlanetData aiPlanetData = new PlanetData();
		aiPlanetData.resourceGenerationSpeed = AiStatManager.getResourceGenerationSpeed(threatLevel);
		aiPlanetData.maxResourceCapacity = AiStatManager.getMaxResourceCapacity(threatLevel);
		
		newAiPlayer.planetData = aiPlanetData;
		newAiPlayer.commandPointsGenerationSpeed = AiStatManager.getCommandPointsGenerationSpeed(threatLevel);
		newAiPlayer.startCommandPoints = AiStatManager.getStartCommandPoints(threatLevel);
		newAiPlayer.maxCommandPoints = AiStatManager.getMaxCommandPoints(threatLevel);
		newAiPlayer.currentCommandPoints = newAiPlayer.startCommandPoints;
		
		difficultyLoader.load(newAiPlayer, threatLevel);
		newAiPlayer.name = "AI (" + newAiPlayer.difficulty.toString() + ")";
		return newAiPlayer;
	}

	private ShipData createAIShip(int threatLevel) {
		ShipData aiShip = new ShipData();
		aiShip.resourceCost = 5;
		aiShip.cpCost = 1;
		aiShip.model = "hawk";
		
		CoreData aiShipCore = new CoreData();
		aiShipCore.type = CoreType.FIGHTER;
		aiShip.core = aiShipCore;
		
		EngineData aiShipEngine = new EngineData();
		aiShipEngine.type = EngineType.ION;
		aiShipEngine.speed = AiStatManager.getMovementSpeed(threatLevel);
		aiShipEngine.acceleration = 1f;
		aiShip.engine = aiShipEngine;
		
		aiShip.weapons = new ArrayList<>();
		aiShip.weapons.add(AiStatManager.getShipWeapon(threatLevel));
		
		aiShip.shields = new ArrayList<>();
		aiShip.shields.add(AiStatManager.getShipShield(threatLevel));
		
		aiShip.hull = AiStatManager.getShipHull(threatLevel);
		return aiShip;
	}
	
	private TurretData createAITurret(int threatLevel) {
		TurretData aiTurret = new TurretData();
		aiTurret.resourceCost = 10;
		aiTurret.cpCost = 1;
		aiTurret.model = "projectile";
		
		PlatformData aiTurretPlatform = new PlatformData();
		aiTurretPlatform.rotationSpeed = 1f;
		aiTurretPlatform.type = PlatformType.SERVO;
		aiTurret.platform = aiTurretPlatform;
		
		aiTurret.weapons = new ArrayList<>();
		aiTurret.weapons.add(AiStatManager.getTurretWeapon(threatLevel));
		
		aiTurret.shields = new ArrayList<>();
		aiTurret.shields.add(AiStatManager.getTurretShield(threatLevel));
		
		aiTurret.hull = AiStatManager.getTurretHull(threatLevel);
		return aiTurret;
	}

	public List<GameOrder> act(AiPlayer aiPlayer, GameState state) {
		List<GameOrder> newOrders = new ArrayList<>();
		
		if (aiPlayer.currentCommandPoints >= 1) {
			GameOrder offensiveOrder = calculateOffensiveOrder(aiPlayer, state);
			if (offensiveOrder != null) newOrders.add(offensiveOrder);
			
			GameOrder defensiveOrder = calculateDefensiveOrder(aiPlayer, state);
			if (defensiveOrder != null) newOrders.add(defensiveOrder);
			
			GameOrder buildOrder = calculateBuildOrder(aiPlayer, state);
			if (buildOrder != null) newOrders.add(buildOrder);
		}
		return newOrders;
	}

	private GameOrder calculateBuildOrder(AiPlayer aiPlayer, GameState state) {
		if (aiPlayer.currentCommandPoints >= 2 && !aiPlayer.turrets.isEmpty()) {
			for (Planet planet : state.planets) {
				Turret turret = EngineUtility.getPlanetTurret(state.turrets, planet.id);
				if (turret == null) {
					if (planet.currentResources >= aiPlayer.turrets.get(0).resourceCost && planet.ownerId == aiPlayer.id) {
						BuildTurretOrder newBuildTurretOrder = new BuildTurretOrder();
						newBuildTurretOrder.planetId = planet.id;
						newBuildTurretOrder.playerId = aiPlayer.id;
						return newBuildTurretOrder;
					}
				}
			}
		}
		return null;
	}

	private GameOrder calculateOffensiveOrder(AiPlayer aiPlayer, GameState state) {
		List<Planet> ownPlanets = new ArrayList<>();
		for (Planet planet : state.planets) {
			if (planet.ownerId == aiPlayer.id) {
				ownPlanets.add(planet);
			}
		}
		float distanceToClosestPlanet = Float.MAX_VALUE;
		Planet closestNeutralPlanet = null;
		Planet closestOwnPlanet = null;
		for (Planet planet : state.planets) {
			Player planetOwner = EngineUtility.getPlayer(state.players, planet.ownerId);
			if (planetOwner.teamId != aiPlayer.teamId && !planet.ancient) {
				for (Planet ownPlanet : ownPlanets) {
					if (distanceToClosestPlanet > ownPlanet.position.dst(planet.position)) {
						distanceToClosestPlanet = ownPlanet.position.dst(planet.position);
						closestNeutralPlanet = planet;
						closestOwnPlanet = ownPlanet;
					}
				}
			}
		}
		if (closestNeutralPlanet != null && closestOwnPlanet != null) {
			if (closestOwnPlanet.currentResources >= aiPlayer.ships.get(0).resourceCost) {
				if (aiPlayer.currentCommandPoints >= 3 || state.maxGameTimeMS - state.gameTimeMS < 15000 && aiPlayer.currentCommandPoints >= 2) {
					ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
					newShipMovementOrder.fromPlanetId = closestOwnPlanet.id;
					newShipMovementOrder.toPlanetId = closestNeutralPlanet.id;
					newShipMovementOrder.playerId = aiPlayer.id;
					newShipMovementOrder.playerShipId = 0;
					return newShipMovementOrder;
				}
			}
		}
		if (state.maxGameTimeMS - state.gameTimeMS < 10000 && state.teamATPs.get(aiPlayer.teamId == 2 ? 1 : 2) == 0) {
			for (Planet planet : state.planets) {
				if (planet.ancient && closestOwnPlanet.currentResources >= aiPlayer.ships.get(0).resourceCost) {
					ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
					newShipMovementOrder.fromPlanetId = closestOwnPlanet.id;
					newShipMovementOrder.toPlanetId = planet.id;
					newShipMovementOrder.playerId = aiPlayer.id;
					newShipMovementOrder.playerShipId = 0;
					return newShipMovementOrder;
				}
			}
		}
		if (state.teamATPs.get(aiPlayer.teamId == 2 ? 1 : 2) >= state.atpToWin / 2) {
			for (Planet planet : state.planets) {
				if (planet.ancient) {
					Player planetOwner = EngineUtility.getPlayer(state.players, planet.ownerId);
					if (planetOwner.teamId != aiPlayer.teamId && closestOwnPlanet.currentResources >= aiPlayer.ships.get(0).resourceCost && aiPlayer.currentCommandPoints > 1) {
						ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
						newShipMovementOrder.fromPlanetId = closestOwnPlanet.id;
						newShipMovementOrder.toPlanetId = planet.id;
						newShipMovementOrder.playerId = aiPlayer.id;
						newShipMovementOrder.playerShipId = 0;
						return newShipMovementOrder;
					}
				}
			}
		}
		return null;
	}
	
	private GameOrder calculateDefensiveOrder(AiPlayer aiPlayer, GameState state) {
		List<Planet> ownPlanets = new ArrayList<>();
		for (Planet planet : state.planets) {
			if (planet.ownerId == aiPlayer.id) {
				ownPlanets.add(planet);
			}
		}
		for (Ship ship : state.ships) {
			Player shipOwner = EngineUtility.getPlayer(state.players, ship.ownerId);
			if (shipOwner.teamId != aiPlayer.teamId) {
				for (Planet ownPlanet : ownPlanets) {
					if (ship.targetPlanetId == ownPlanet.id) {
						if (ownPlanet.position.dst(ship.position) <= aiPlayer.behaviorParameters.defensiveShipDistanceThreshold) {
							if (ownPlanet.currentResources >= aiPlayer.ships.get(0).resourceCost) {
								ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
								newShipMovementOrder.fromPlanetId = ownPlanet.id;
								newShipMovementOrder.toPlanetId = ship.originPlanetId;
								newShipMovementOrder.playerId = aiPlayer.id;
								newShipMovementOrder.playerShipId = 0;
								return newShipMovementOrder;
							}
						}
					}
				}
			}
		}
		return null;
	}

}
