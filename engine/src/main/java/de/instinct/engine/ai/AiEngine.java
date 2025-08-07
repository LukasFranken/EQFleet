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
import de.instinct.engine.model.planet.TurretData;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.ShipType;
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
		ShipData aiShip = new ShipData();
		aiShip.cost = 5;
		aiShip.commandPointsCost = 1;
		aiShip.model = "hawk";
		
		aiShip.movementSpeed = AiStatManager.getMovementSpeed(threatLevel);
		aiShip.type = ShipType.FIGHTER;
		
		aiShip.weapon = AiStatManager.getShipWeapon(threatLevel);
		aiShip.defense = AiStatManager.getShipDefense(threatLevel);
		newAiPlayer.ships.add(aiShip);
		
		PlanetData aiPlanetData = new PlanetData();
		aiPlanetData.resourceGenerationSpeed = 1;
		aiPlanetData.maxResourceCapacity = 10;
		
		if (threatLevel >= 10) {
			TurretData aiTurret = new TurretData();
			aiTurret.model = "hawk";
			aiTurret.weapon = AiStatManager.getPlanetWeapon(threatLevel);
			aiTurret.defense = AiStatManager.getPlanetDefense(threatLevel);
			aiTurret.commandPointsCost = 1;
			aiTurret.cost = 10;
			aiTurret.rotationSpeed = 0.1f;
			aiPlanetData.turret = aiTurret;
		}
		
		newAiPlayer.planetData = aiPlanetData;
		newAiPlayer.commandPointsGenerationSpeed = 0.1f;
		newAiPlayer.startCommandPoints = 3;
		newAiPlayer.maxCommandPoints = 10;
		newAiPlayer.currentCommandPoints = newAiPlayer.startCommandPoints;
		
		difficultyLoader.load(newAiPlayer, threatLevel);
		newAiPlayer.name = "AI (" + newAiPlayer.difficulty.toString() + ")";
		return newAiPlayer;
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
		if (aiPlayer.currentCommandPoints >= 2) {
			for (Planet planet : state.planets) {
				Turret turret = EngineUtility.getPlanetTurret(state.turrets, planet.id);
				if (turret == null) {
					if (planet.currentResources >= aiPlayer.planetData.turret.cost) {
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
			if (closestOwnPlanet.currentResources >= aiPlayer.ships.get(0).cost) {
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
				if (planet.ancient) {
					ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
					newShipMovementOrder.fromPlanetId = closestOwnPlanet.id;
					newShipMovementOrder.toPlanetId = planet.id;
					newShipMovementOrder.playerId = aiPlayer.id;
					newShipMovementOrder.playerShipId = 0;
					return newShipMovementOrder;
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
							if (ownPlanet.currentResources >= aiPlayer.ships.get(0).cost) {
								ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
								newShipMovementOrder.fromPlanetId = ownPlanet.id;
								newShipMovementOrder.toPlanetId = ship.planetId;
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
