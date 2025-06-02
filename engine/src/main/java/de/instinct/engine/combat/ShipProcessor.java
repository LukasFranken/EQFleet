package de.instinct.engine.combat;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.entity.Unit;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine.util.VectorUtil;

public class ShipProcessor {
	
	private WeaponProcessor weaponProcessor;
	
	public ShipProcessor() {
		weaponProcessor = new WeaponProcessor();
	}

	public void updateShips(Combat combat, GameState state, long deltaTime) {
		List<Ship> shipsToRemove = new ArrayList<>();
		for (Ship ship : combat.ships) {
			if (ship.currentArmor <= 0) {
				shipsToRemove.add(ship);
				continue;
			}
			Unit closestInRangeTarget = weaponProcessor.getClosestInRangeTarget(ship, state, combat);
			if (closestInRangeTarget != null) {
				weaponProcessor.fireAtTarget(ship, closestInRangeTarget, combat, deltaTime);
			} else {
				if (moveShip(ship, state, deltaTime)) {
					shipsToRemove.add(ship);
				}
			}
		}
		for (Ship ship : shipsToRemove) {
			combat.ships.remove(ship);
		}
	}
	
	public Ship createShipInstance(ShipMovementOrder movement, GameState state) {
		Planet fromPlanet = EngineUtility.getPlanet(state.planets, movement.fromPlanetId);
		Planet toPlanet = EngineUtility.getPlanet(state.planets, movement.toPlanetId);
		Player player = EngineUtility.getPlayer(state.players, movement.playerId);
		Ship newShip = EntityManager.createShip(player.ships.get(movement.playerShipId));
		player.currentCommandPoints -= player.ships.get(movement.playerShipId).commandPointsCost;
		newShip.ownerId = movement.playerId;
		newShip.position = VectorUtil.getTargetPosition(fromPlanet.position, toPlanet.position, EngineUtility.PLANET_RADIUS);
		newShip.targetPlanetId = movement.toPlanetId;
		return newShip;
	}
	
	private boolean moveShip(Ship ship, GameState state, long deltaTime) {
		Player shipOwner = EngineUtility.getPlayer(state.players, ship.ownerId);
		Planet targetPlanet = EngineUtility.getPlanet(state.planets, ship.targetPlanetId);
		Player targetPlanetOwner = EngineUtility.getPlayer(state.players, targetPlanet.ownerId);
		Vector2 targetPosition = VectorUtil.getTargetPosition(ship.position, targetPlanet.position, ((float)deltaTime / 1000f) * ship.movementSpeed);
		ship.position = targetPosition;
		if (EntityManager.entityDistance(ship, targetPlanet) <= 0) {
			if (targetPlanetOwner.teamId == shipOwner.teamId) {
				targetPlanet.currentResources += ship.cost;
				if (targetPlanet.currentResources > targetPlanet.maxResourceCapacity) {
					targetPlanet.currentResources = targetPlanet.maxResourceCapacity;
				}
			} else if (targetPlanet.defense == null) {
				conquerPlanet(targetPlanet, shipOwner);
				targetPlanet.currentResources += ship.cost;
			}
			return true;
		}
		return false;
	}
	
	private void conquerPlanet(Planet planet, Player newOwner) {
		planet.ownerId = newOwner.id;
		planet.currentWeaponCooldown = 0;
		planet.currentShield = 0;
		planet.defense = newOwner.planetData.defense;
		planet.weapon = newOwner.planetData.weapon;
		planet.currentArmor = newOwner.planetData.defense.armor * newOwner.planetData.percentOfArmorAfterCapture;
		planet.resourceGenerationSpeed = newOwner.planetData.resourceGenerationSpeed;
		planet.maxResourceCapacity = newOwner.planetData.maxResourceCapacity;
		planet.currentResources = 0;
	}

}
