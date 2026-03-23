package de.instinct.engine.combat;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.unit.UnitProcessor;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.player.PlayerProcessor;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.unit.ShipStatistic;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine.util.VectorUtil;

public class ShipProcessor {

	public static void updateShips(GameState state, long deltaTime) {
		for (Ship ship : state.entityData.ships) {
			updateShip(ship, state, deltaTime);
		}
		EntityProcessor.removeDestroyed(state.entityData.ships.iterator());
	}
	
	private static void updateShip(Ship ship, GameState state, long deltaTime) {
		UnitProcessor.updateUnit(ship, state, deltaTime);
		if (!UnitProcessor.isInCombatRange(ship, state)) {
			moveShip(ship, state, deltaTime);
		}
	}

	public static void createShipInstance(ShipMovementOrder movement, GameState state) {
		Planet fromPlanet = EngineUtility.getPlanet(state.entityData.planets, movement.fromPlanetId);
		Planet toPlanet = EngineUtility.getPlanet(state.entityData.planets, movement.toPlanetId);
		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, movement.playerId);
		ShipData shipData = player.ships.get(movement.playerShipId);
		Ship newShip = new Ship();
		UnitProcessor.initializeUnit(newShip, shipData, movement.fromPlanetId, state, true);
		newShip.position = VectorUtil.getTargetPosition(fromPlanet.position, toPlanet.position, EngineUtility.PLANET_RADIUS);
		newShip.targetPlanetId = movement.toPlanetId;
		newShip.radius = 3;
		state.entityData.ships.add(newShip);
		
		ShipStatistic shipStatistic = StatCollector.getPlayer(state.gameUUID, newShip.ownerId).getShip(newShip.data.model);
		shipStatistic.getCoreStatistic().setTimesBuilt(shipStatistic.getCoreStatistic().getTimesBuilt() + 1);
		shipStatistic.getCoreStatistic().setResourcesUsed(shipStatistic.getCoreStatistic().getResourcesUsed() + shipData.resourceCost);
	}
	
	private static void moveShip(Ship ship, GameState state, long deltaTime) {
		Player shipOwner = EngineUtility.getPlayer(state.staticData.playerData.players, ship.ownerId);
		Planet targetPlanet = EngineUtility.getPlanet(state.entityData.planets, ship.targetPlanetId);
		Player targetPlanetOwner = EngineUtility.getPlayer(state.staticData.playerData.players, targetPlanet.ownerId);
		ShipData shipData = (ShipData) ship.data;
		float distance = Math.min(((float)deltaTime / 1000f) * shipData.speed, VectorUtil.dst(ship.position, targetPlanet.position));
		Vector2 targetPosition = VectorUtil.getTargetPosition(ship.position, targetPlanet.position, distance);
		ship.position = targetPosition;
		ShipStatistic shipStatistic = StatCollector.getPlayer(state.gameUUID, shipOwner.id).getShip(ship.data.model);
		shipStatistic.getEngineStatistic().setDistanceTraveled(shipStatistic.getEngineStatistic().getDistanceTraveled() + distance);
		if (EntityManager.entityDistance(ship, targetPlanet) <= 0.01) {
			if (targetPlanetOwner.teamId != shipOwner.teamId) {
				targetPlanet.ownerId = shipOwner.id;
				targetPlanet.resourceGenerationSpeed = shipOwner.planetData.baseResourceGenerationSpeed;
				targetPlanetOwner = shipOwner;
			}
			PlayerProcessor.addResources(targetPlanetOwner, shipData.resourceCost);
			ship.flaggedForDestroy = true;
		}
	}


}
