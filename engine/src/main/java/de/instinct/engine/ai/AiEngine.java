package de.instinct.engine.ai;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.ai.difficulty.AiDifficulty;
import de.instinct.engine.ai.difficulty.AiStatManager;
import de.instinct.engine.ai.difficulty.DifficultyLoader;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.PlanetData;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.ShipType;
import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.model.ship.WeaponType;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.util.EngineUtility;

public class AiEngine {
	
	private DifficultyLoader difficultyLoader;
	
	public AiEngine() {
		difficultyLoader = new DifficultyLoader();
	}
	
	public AiPlayer initialize(AiDifficulty difficulty, int threatLevel) {
		AiPlayer newAiPlayer = new AiPlayer();
		difficultyLoader.load(newAiPlayer, difficulty);
		
		newAiPlayer.name = "AI (" + difficulty.toString() + ")";
		newAiPlayer.ships = new ArrayList<>();
		ShipData aiShip = new ShipData();
		aiShip.cost = 5;
		aiShip.commandPointsCost = 1;
		aiShip.model = "hawk";
		
		aiShip.movementSpeed = AiStatManager.getMovementSpeed(threatLevel);
		aiShip.type = ShipType.FIGHTER;
		
		aiShip.weapon = AiStatManager.getShipWeapon(threatLevel);
		Defense aiShipDefense = new Defense();
		aiShipDefense.shield = 4;
		aiShipDefense.armor = 4;
		aiShipDefense.shieldRegenerationSpeed = 0.2f;
		aiShip.defense = aiShipDefense;
		newAiPlayer.ships.add(aiShip);
		
		PlanetData aiPlanetData = new PlanetData();
		aiPlanetData.resourceGenerationSpeed = 1;
		aiPlanetData.maxResourceCapacity = 10;
		aiPlanetData.percentOfArmorAfterCapture = 0.2f;
		Weapon aiPlanetWeapon = new Weapon();
		aiPlanetWeapon.type = WeaponType.LASER;
		aiPlanetWeapon.damage = 3;
		aiPlanetWeapon.range = 100f;
		aiPlanetWeapon.cooldown = 1000;
		aiPlanetWeapon.speed = 50f;
		aiPlanetData.weapon = aiPlanetWeapon;
		Defense aiPlanetDefense = new Defense();
		aiPlanetDefense.shield = 15;
		aiPlanetDefense.armor = 20;
		aiPlanetDefense.shieldRegenerationSpeed = 0.5f;
		aiPlanetData.defense = aiPlanetDefense;
		
		newAiPlayer.planetData = aiPlanetData;
		newAiPlayer.commandPointsGenerationSpeed = 0.1f;
		newAiPlayer.startCommandPoints = 3;
		newAiPlayer.maxCommandPoints = 10;
		newAiPlayer.currentCommandPoints = newAiPlayer.startCommandPoints;
		return newAiPlayer;
	}
	
	public List<GameOrder> act(AiPlayer aiPlayer, GameState state) {
		List<GameOrder> newOrders = new ArrayList<>();
		
		if (aiPlayer.currentCommandPoints >= 1) {
			GameOrder offensiveOrder = calculateOffensiveOrder(aiPlayer, state);
			if (offensiveOrder != null) newOrders.add(offensiveOrder);
			
			GameOrder defensiveOrder = calculateDefensiveOrder(aiPlayer, state);
			if (defensiveOrder != null) newOrders.add(defensiveOrder);
		}
		return newOrders;
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
				if (aiPlayer.currentCommandPoints >= 3) {
					ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
					newShipMovementOrder.fromPlanetId = closestOwnPlanet.id;
					newShipMovementOrder.toPlanetId = closestNeutralPlanet.id;
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
