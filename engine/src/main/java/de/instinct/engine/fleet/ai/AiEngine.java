package de.instinct.engine.fleet.ai;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.ai.data.AiPlayer;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.entity.unit.ship.Ship;
import de.instinct.engine.fleet.entity.unit.turret.Turret;
import de.instinct.engine.fleet.entity.unit.turret.TurretProcessor;
import de.instinct.engine.fleet.order.types.BuildTurretOrder;
import de.instinct.engine.fleet.order.types.ShipMovementOrder;
import de.instinct.engine.fleet.player.FleetPlayerProcessor;

public class AiEngine {
	
	private FleetPlayerProcessor playerProcessor;
	private TurretProcessor turretProcessor;
	
	public AiEngine() {
		playerProcessor = new FleetPlayerProcessor();
		turretProcessor = new TurretProcessor();
	}

	public List<GameOrder> act(AiPlayer aiPlayer, FleetGameState state) {
		List<GameOrder> newOrders = new ArrayList<>();
		
		GameOrder offensiveOrder = calculateOffensiveOrder(aiPlayer, state);
		if (offensiveOrder != null) newOrders.add(offensiveOrder);
		
		GameOrder defensiveOrder = calculateDefensiveOrder(aiPlayer, state);
		if (defensiveOrder != null) newOrders.add(defensiveOrder);
		
		GameOrder buildOrder = calculateBuildOrder(aiPlayer, state);
		if (buildOrder != null) newOrders.add(buildOrder);
		return newOrders;
	}

	private GameOrder calculateBuildOrder(AiPlayer aiPlayer, FleetGameState state) {
		if (aiPlayer.turrets.isEmpty()) return null;
		if (aiPlayer.currentResources >= aiPlayer.turrets.get(0).resourceCost * 2 && !aiPlayer.turrets.isEmpty()) {
			for (Planet planet : state.entityData.planets) {
				Turret turret = turretProcessor.getPlanetTurret(state.entityData.turrets, planet.id);
				if (turret == null) {
					if (planet.ownerId == aiPlayer.id) {
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

	private GameOrder calculateOffensiveOrder(AiPlayer aiPlayer, FleetGameState state) {
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
			Player planetOwner = playerProcessor.getPlayer(state.playerData.players, planet.ownerId);
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
			if (aiPlayer.currentResources >= aiPlayer.ships.get(0).resourceCost * 3 || state.staticData.maxGameTimeMS - state.metaData.gameTimeMS < 15000 && aiPlayer.currentResources >= aiPlayer.ships.get(0).resourceCost * 2) {
				ShipMovementOrder newShipMovementOrder = new ShipMovementOrder();
				newShipMovementOrder.fromPlanetId = closestOwnPlanet.id;
				newShipMovementOrder.toPlanetId = closestNeutralPlanet.id;
				newShipMovementOrder.playerId = aiPlayer.id;
				newShipMovementOrder.playerShipId = 0;
				return newShipMovementOrder;
			}
		}
		if (state.staticData.maxGameTimeMS - state.metaData.gameTimeMS < 10000 && state.teamATPs.get(aiPlayer.teamId == 2 ? 1 : 2) == 0) {
			for (Planet planet : state.entityData.planets) {
				if (planet.ancient && closestOwnPlanet != null && aiPlayer.currentResources >= aiPlayer.ships.get(0).resourceCost) {
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
					Player planetOwner = playerProcessor.getPlayer(state.playerData.players, planet.ownerId);
					if (planetOwner.teamId != aiPlayer.teamId && aiPlayer.currentResources >= aiPlayer.ships.get(0).resourceCost) {
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
	
	private GameOrder calculateDefensiveOrder(AiPlayer aiPlayer, FleetGameState state) {
		List<Planet> ownPlanets = new ArrayList<>();
		for (Planet planet : state.entityData.planets) {
			if (planet.ownerId == aiPlayer.id) {
				ownPlanets.add(planet);
			}
		}
		for (Ship ship : state.entityData.ships) {
			Player shipOwner = playerProcessor.getPlayer(state.playerData.players, ship.ownerId);
			if (shipOwner.teamId != aiPlayer.teamId) {
				for (Planet ownPlanet : ownPlanets) {
					if (ship.targetPlanetId == ownPlanet.id) {
						if (ownPlanet.position.dst(ship.position) <= aiPlayer.behaviorParameters.defensiveShipDistanceThreshold) {
							if (aiPlayer.currentResources >= aiPlayer.ships.get(0).resourceCost) {
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
