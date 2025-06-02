package de.instinct.engine.ai;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.PlanetData;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.ShipType;
import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.model.ship.WeaponType;
import de.instinct.engine.order.GameOrder;

public class AiEngine {
	
	public AiPlayer initialize(AiDifficulty difficulty) {
		AiPlayer newAiPlayer = new AiPlayer();
		newAiPlayer.difficulty = difficulty;
		
		newAiPlayer.name = "AI (" + difficulty.toString() + ")";
		newAiPlayer.ships = new ArrayList<>();
		ShipData aiShip = new ShipData();
		aiShip.cost = 5;
		aiShip.commandPointsCost = 1;
		aiShip.model = "hawk";
		aiShip.movementSpeed = 2000;
		aiShip.type = ShipType.FIGHTER;
		Weapon aiShipWeapon = new Weapon();
		aiShipWeapon.type = WeaponType.LASER;
		aiShipWeapon.damage = 2;
		aiShipWeapon.range = 60f;
		aiShipWeapon.cooldown = 500;
		aiShipWeapon.speed = 100f;
		aiShip.weapon = aiShipWeapon;
		Defense aiShipDefense = new Defense();
		aiShipDefense.shield = 2;
		aiShipDefense.armor = 3;
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
		aiPlanetWeapon.range = 60f;
		aiPlanetWeapon.cooldown = 2000;
		aiPlanetWeapon.speed = 50f;
		aiPlanetData.weapon = aiPlanetWeapon;
		Defense aiPlanetDefense = new Defense();
		aiPlanetDefense.shield = 10;
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
		List<GameOrder> orders = new ArrayList<>();
		if (aiPlayer.currentCommandPoints > 0) {
			List<Planet> ownPlanets = new ArrayList<>();
			for (Planet planet : state.planets) {
				if (planet.ownerId == aiPlayer.id) {
					ownPlanets.add(planet);
				}
			}
			/*List<ShipMovementEvent> capturePlanetTargets = new ArrayList<>();
			for (Planet planet : state.planets) {
				if (planet.ownerId != aiPlayer.playerId && !planet.ancient) {
					for (Planet ownPlanet : ownPlanets) {
						ShipMovementEvent capturePlanetTarget = new ShipMovementEvent();
						capturePlanetTarget.playerId = aiPlayer.playerId;
						capturePlanetTarget.fromPlanetId = ownPlanet.id;
						capturePlanetTarget.toPlanetId = planet.id;
						capturePlanetTarget.shipData = aiPlayer.ships.get(0);
						if (planet.ownerId == 0) {
							if (planet.currentHealth < (int)(aiPlayer.ships.get(0).weapon.damage)) {
								capturePlanetTargets.add(capturePlanetTarget);
							}
						}
					}
				}
			}
			ShipMovementEvent lowestUnorderedPlanetTarget = null;
			for (ShipMovementEvent fleetMovementEvent : capturePlanetTargets) {
				boolean orderToPlanetExists = false;
				for (GameEvent event : state.activeEvents) {
					if (event instanceof ShipMovementEvent) {
						if (((ShipMovementEvent)event).playerId == aiPlayer.playerId) {
							if (fleetMovementEvent.toPlanetId == ((ShipMovementEvent)event).toPlanetId) {
								orderToPlanetExists = true;
							}
						}
					}
				}
					
				if (!orderToPlanetExists) {
					if (lowestUnorderedPlanetTarget == null) {
						lowestUnorderedPlanetTarget = fleetMovementEvent;
					} else {
						if (EngineUtility.calculateTotalTravelTimeMS(state, fleetMovementEvent) < EngineUtility.calculateTotalTravelTimeMS(state, lowestUnorderedPlanetTarget)) {
							lowestUnorderedPlanetTarget = fleetMovementEvent;
						}
					}
				}
			}
			if (lowestUnorderedPlanetTarget != null) {
				ShipMovementOrder newCaptureOrder = new ShipMovementOrder();
				newCaptureOrder.playerId = aiPlayer.playerId;
				newCaptureOrder.fromPlanetId = lowestUnorderedPlanetTarget.fromPlanetId;
				newCaptureOrder.toPlanetId = lowestUnorderedPlanetTarget.toPlanetId;
				orders.add(newCaptureOrder);
			}*/
		}
		return orders;
	}

}
