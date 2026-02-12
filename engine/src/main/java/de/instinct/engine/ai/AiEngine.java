package de.instinct.engine.ai;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.BuildTurretOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.util.EngineUtility;

public class AiEngine {

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
			for (Planet planet : state.entityData.planets) {
				Turret turret = EngineUtility.getPlanetTurret(state.entityData.turrets, planet.id);
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
		for (Planet planet : state.entityData.planets) {
			if (planet.ownerId == aiPlayer.id) {
				ownPlanets.add(planet);
			}
		}
		float distanceToClosestPlanet = Float.MAX_VALUE;
		Planet closestNeutralPlanet = null;
		Planet closestOwnPlanet = null;
		for (Planet planet : state.entityData.planets) {
			Player planetOwner = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
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
				if (aiPlayer.currentCommandPoints >= 3 || state.staticData.maxGameTimeMS - state.gameTimeMS < 15000 && aiPlayer.currentCommandPoints >= 2) {
					ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
					newShipMovementOrder.fromPlanetId = closestOwnPlanet.id;
					newShipMovementOrder.toPlanetId = closestNeutralPlanet.id;
					newShipMovementOrder.playerId = aiPlayer.id;
					newShipMovementOrder.playerShipId = 0;
					return newShipMovementOrder;
				}
			}
		}
		if (state.staticData.maxGameTimeMS - state.gameTimeMS < 10000 && state.teamATPs.get(aiPlayer.teamId == 2 ? 1 : 2) == 0) {
			for (Planet planet : state.entityData.planets) {
				if (planet.ancient && closestOwnPlanet != null && closestOwnPlanet.currentResources >= aiPlayer.ships.get(0).resourceCost) {
					ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
					newShipMovementOrder.fromPlanetId = closestOwnPlanet.id;
					newShipMovementOrder.toPlanetId = planet.id;
					newShipMovementOrder.playerId = aiPlayer.id;
					newShipMovementOrder.playerShipId = 0;
					return newShipMovementOrder;
				}
			}
		}
		if (state.teamATPs.get(aiPlayer.teamId == 2 ? 1 : 2) >= state.staticData.atpToWin / 2) {
			for (Planet planet : state.entityData.planets) {
				if (planet.ancient) {
					Player planetOwner = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
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
		for (Planet planet : state.entityData.planets) {
			if (planet.ownerId == aiPlayer.id) {
				ownPlanets.add(planet);
			}
		}
		for (Ship ship : state.entityData.ships) {
			Player shipOwner = EngineUtility.getPlayer(state.staticData.playerData.players, ship.ownerId);
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
