package de.instinct.engine.fleet.entity.unit.ship;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.util.VectorUtil;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.entity.planet.PlanetProcessor;
import de.instinct.engine.fleet.entity.unit.UnitProcessor;
import de.instinct.engine.fleet.entity.unit.ship.data.ShipData;
import de.instinct.engine.fleet.order.types.ShipMovementOrder;
import de.instinct.engine.fleet.player.FleetPlayer;
import de.instinct.engine.fleet.player.FleetPlayerProcessor;
import de.instinct.engine.fleet.stats.StatCollector;
import de.instinct.engine.fleet.stats.model.unit.ShipStatistic;

public class ShipProcessor {
	
	private UnitProcessor unitProcessor;
	private PlanetProcessor planetProcessor;
	private FleetPlayerProcessor playerProcessor;
	
	public ShipProcessor() {
		unitProcessor = new UnitProcessor();
		planetProcessor = new PlanetProcessor();
		playerProcessor = new FleetPlayerProcessor();
	}

	public void updateShips(FleetGameState state, long deltaTime) {
		for (Ship ship : state.entityData.ships) {
			updateShip(ship, state, deltaTime);
		}
		unitProcessor.removeDestroyed(state.entityData.ships.iterator());
	}
	
	private void updateShip(Ship ship, FleetGameState state, long deltaTime) {
		unitProcessor.updateUnit(ship, state, deltaTime);
		if (!unitProcessor.isInCombatRange(ship, state)) {
			moveShip(ship, state, deltaTime);
		}
	}

	public void createShipInstance(ShipMovementOrder movement, FleetGameState state) {
		Planet fromPlanet = planetProcessor.getPlanet(state.entityData.planets, movement.fromPlanetId);
		Planet toPlanet = planetProcessor.getPlanet(state.entityData.planets, movement.toPlanetId);
		FleetPlayer player = playerProcessor.getFleetPlayer(state.playerData.players, movement.playerId);
		ShipData shipData = player.ships.get(movement.playerShipId);
		Ship newShip = new Ship();
		unitProcessor.initializeUnit(newShip, shipData, fromPlanet, state, true);
		newShip.position = VectorUtil.getTargetPosition(fromPlanet.position, toPlanet.position, PlanetProcessor.PLANET_RADIUS);
		newShip.targetPlanetId = movement.toPlanetId;
		newShip.radius = 3;
		state.entityData.ships.add(newShip);
		
		ShipStatistic shipStatistic = StatCollector.getPlayer(state.gameUUID, newShip.ownerId).getShip(newShip.data.model);
		shipStatistic.getCoreStatistic().setTimesBuilt(shipStatistic.getCoreStatistic().getTimesBuilt() + 1);
		shipStatistic.getCoreStatistic().setResourcesUsed(shipStatistic.getCoreStatistic().getResourcesUsed() + shipData.resourceCost);
	}
	
	private void moveShip(Ship ship, FleetGameState state, long deltaTime) {
		FleetPlayer shipOwner = playerProcessor.getFleetPlayer(state.playerData.players, ship.ownerId);
		Planet targetPlanet = planetProcessor.getPlanet(state.entityData.planets, ship.targetPlanetId);
		FleetPlayer targetPlanetOwner = playerProcessor.getFleetPlayer(state.playerData.players, targetPlanet.ownerId);
		ShipData shipData = (ShipData) ship.data;
		float distance = Math.min(((float)deltaTime / 1000f) * shipData.speed, VectorUtil.dst(ship.position, targetPlanet.position));
		Vector2 targetPosition = VectorUtil.getTargetPosition(ship.position, targetPlanet.position, distance);
		ship.position = targetPosition;
		ShipStatistic shipStatistic = StatCollector.getPlayer(state.gameUUID, shipOwner.id).getShip(ship.data.model);
		shipStatistic.getEngineStatistic().setDistanceTraveled(shipStatistic.getEngineStatistic().getDistanceTraveled() + distance);
		if (unitProcessor.entityDistance(ship, targetPlanet) <= 0.01) {
			if (targetPlanetOwner.teamId != shipOwner.teamId) {
				planetProcessor.transferPlanetControl(state, targetPlanet, shipOwner);
				targetPlanetOwner = shipOwner;
			}
			playerProcessor.addResources(targetPlanetOwner, shipData.resourceCost * shipData.transferRate);
			ship.flaggedForDestroy = true;
		}
	}


}
