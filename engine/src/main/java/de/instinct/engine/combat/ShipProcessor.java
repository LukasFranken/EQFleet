package de.instinct.engine.combat;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.unit.UnitProcessor;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.unit.ShipStatistic;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine.util.VectorUtil;

public class ShipProcessor extends UnitProcessor {
	
	public ShipProcessor() {
		super();
	}

	public void updateShips(GameState state, long deltaTime) {
		for (Ship ship : state.ships) {
			updateShip(ship, state, deltaTime);
		}
		super.removeDestroyed(state.ships.iterator());
	}
	
	private void updateShip(Ship ship, GameState state, long deltaTime) {
		super.updateUnit(ship, state, deltaTime);
		if (super.getClosestInRangeTarget(ship, ship.weapon.range, state) == null) {
			moveShip(ship, state, deltaTime);
		}
	}

	public void createShipInstance(ShipMovementOrder movement, GameState state) {
		Planet fromPlanet = EngineUtility.getPlanet(state.planets, movement.fromPlanetId);
		Planet toPlanet = EngineUtility.getPlanet(state.planets, movement.toPlanetId);
		Player player = EngineUtility.getPlayer(state.players, movement.playerId);
		ShipData shipData = player.ships.get(movement.playerShipId);
		Ship newShip = new Ship();
		super.initializeUnit(newShip, shipData, movement.fromPlanetId, state, true);
		newShip.position = VectorUtil.getTargetPosition(fromPlanet.position, toPlanet.position, EngineUtility.PLANET_RADIUS);
		newShip.targetPlanetId = movement.toPlanetId;
		newShip.type = shipData.type;
		newShip.movementSpeed = shipData.movementSpeed;
		newShip.radius = 3;
		state.ships.add(newShip);
	}
	
	private void moveShip(Ship ship, GameState state, long deltaTime) {
		Player shipOwner = EngineUtility.getPlayer(state.players, ship.ownerId);
		Planet targetPlanet = EngineUtility.getPlanet(state.planets, ship.targetPlanetId);
		Player targetPlanetOwner = EngineUtility.getPlayer(state.players, targetPlanet.ownerId);
		float distance = Math.min(((float)deltaTime / 1000f) * ship.movementSpeed, VectorUtil.dst(ship.position, targetPlanet.position));
		Vector2 targetPosition = VectorUtil.getTargetPosition(ship.position, targetPlanet.position, distance);
		ship.position = targetPosition;
		ShipStatistic shipStatistic = StatCollector.getPlayer(state.gameUUID, shipOwner.id).getShip(ship.model);
		shipStatistic.setDistanceTraveled(shipStatistic.getDistanceTraveled() + distance);
		if (EntityManager.entityDistance(ship, targetPlanet) <= 0.01) {
			if (targetPlanetOwner.teamId == shipOwner.teamId) {
				targetPlanet.currentResources += ship.cost;
				if (targetPlanet.currentResources > targetPlanet.maxResourceCapacity) {
					targetPlanet.currentResources = targetPlanet.maxResourceCapacity;
				}
			} else {
				conquerPlanet(targetPlanet, shipOwner);
				targetPlanet.currentResources += ship.cost;
			}
			ship.flaggedForDestroy = true;
		}
	}
	
	private void conquerPlanet(Planet planet, Player newOwner) {
		planet.ownerId = newOwner.id;
		planet.resourceGenerationSpeed = newOwner.planetData.resourceGenerationSpeed;
		planet.maxResourceCapacity = newOwner.planetData.maxResourceCapacity;
		planet.currentResources = 0;
	}

}
