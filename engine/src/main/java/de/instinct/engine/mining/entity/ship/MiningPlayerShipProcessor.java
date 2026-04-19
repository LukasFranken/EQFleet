package de.instinct.engine.mining.entity.ship;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.entity.ship.cargo.MiningCargo;
import de.instinct.engine.mining.entity.ship.core.MiningCore;
import de.instinct.engine.mining.entity.ship.core.MiningCoreProcessor;
import de.instinct.engine.mining.entity.ship.thruster.MiningThruster;
import de.instinct.engine.mining.entity.ship.thruster.MiningThrusterProcessor;
import de.instinct.engine.mining.entity.ship.weapon.MiningWeapon;
import de.instinct.engine.mining.entity.ship.weapon.MiningWeaponProcessor;
import de.instinct.engine.mining.order.InputChangedOrder;
import de.instinct.engine.mining.order.RecallOrder;
import de.instinct.engine.mining.player.MiningPlayer;
import de.instinct.engine.mining.player.MiningPlayerProcessor;

public class MiningPlayerShipProcessor extends EntityProcessor {
	
	private MiningPlayerProcessor playerProcessor;
	private MiningWeaponProcessor weaponProcessor;
	private MiningThrusterProcessor thrusterProcessor;
	private MiningCoreProcessor coreProcessor;
	
	public MiningPlayerShipProcessor() {
		playerProcessor = new MiningPlayerProcessor();
		weaponProcessor = new MiningWeaponProcessor();
		thrusterProcessor = new MiningThrusterProcessor();
		coreProcessor = new MiningCoreProcessor();
	}

	public void update(MiningGameState state, long progressionMS) {
		for (MiningPlayerShip ship : state.entityData.playerShips) {
			updateShip(state, ship, progressionMS);
		}
	}

	private void updateShip(MiningGameState state, MiningPlayerShip ship, long progressionMS) {
		super.updateEntity(ship, state, progressionMS);
		MiningPlayer shipOwner = playerProcessor.getMiningPlayer(state.playerData.players, ship.ownerId);
		thrusterProcessor.updateThruster(shipOwner, ship, progressionMS);
		weaponProcessor.updateWeapons(state, shipOwner, ship, progressionMS);
		coreProcessor.updateCore(shipOwner, ship, progressionMS);
	}
	
	public void createPlayerShips(MiningGameState state) {
		for (Player player : state.playerData.players) {
			MiningPlayer miningPlayer = (MiningPlayer) player;
			MiningPlayerShip ship = createPlayerShip(miningPlayer);
			super.initializeEntity(ship, state.entityData);
			state.entityData.playerShips.add(ship);
		}
	}

	public MiningPlayerShip createPlayerShip(MiningPlayer player) {
		MiningPlayerShip ship = new MiningPlayerShip();
		ship.ownerId = player.id;
		ship.position.set(0, 0);
		ship.radius = 10f;
		ship.direction = new Vector2(0, 1);
		
		ship.core = new MiningCore();
		ship.core.maxCharge = player.shipData.coreCharge;
		ship.core.currentCharge = ship.core.maxCharge;
		
		ship.cargo = new MiningCargo();
		ship.cargo.capacity = player.shipData.cargoCapacity;
		ship.cargo.items = new ArrayList<>();
		
		ship.thruster = new MiningThruster();
		ship.thruster.acceleration = player.shipData.acceleration;
		ship.thruster.maxSpeed = player.shipData.maxSpeed;
		ship.thruster.deceleration = player.shipData.deceleration;
		ship.thruster.maxReverseSpeed = player.shipData.maxSpeedReverse;
		ship.thruster.rotationAcceleration = player.shipData.rotationAcceleration;
		ship.thruster.maxRotationSpeed = player.shipData.maxRotationSpeed;
		ship.thruster.chargePerSecond = player.shipData.chargePerSecond;
		ship.thruster.inertiaDampening = player.shipData.inertiaDampening;
		
		ship.weapon = new MiningWeapon();
		ship.weapon.cooldownMS = player.shipData.cooldownMS;
		ship.weapon.lifetimeMS = player.shipData.lifetimeMS;
		ship.weapon.damage = player.shipData.damage;
		ship.weapon.speed = player.shipData.projectileSpeed;
		ship.weapon.chargePerShot = player.shipData.chargePerShot;
		return ship;
	}

	public boolean integrateNewOrder(MiningGameState state, GameOrder order) {
		if (order instanceof InputChangedOrder) {
			InputChangedOrder inputOrder = (InputChangedOrder) order;
			if (isValid(state, inputOrder)) {
				for (Player player : state.playerData.players) {
					if (player.id == inputOrder.playerId) {
						MiningPlayer miningPlayer = (MiningPlayer) player;
						miningPlayer.forward = inputOrder.forward;
						miningPlayer.backward = inputOrder.backward;
						miningPlayer.left = inputOrder.left;
						miningPlayer.right = inputOrder.right;
						miningPlayer.shoot = inputOrder.shoot;
						break;
					}
				}
			}
			return true;
		}
		if (order instanceof RecallOrder) {
			RecallOrder recallOrder = (RecallOrder) order;
			if (isValid(state, recallOrder)) {
				MiningPlayerShip ship = getPlayerShip(state, recallOrder.playerId);
				ship.recalled = !ship.recalled;
			}
			return true;
		}
		return false;
	}

	private boolean isValid(MiningGameState state, InputChangedOrder inputOrder) {
		MiningPlayerShip ship = getPlayerShip(state, inputOrder.playerId);
		if (ship == null) return false;
		if (ship.recalled) return false;
		return true;
	}
	
	private boolean isValid(MiningGameState state, RecallOrder recallOrder) {
		MiningPlayerShip ship = getPlayerShip(state, recallOrder.playerId);
		if (ship == null) return false;
		if (!shipIsInRecallArea(state, ship)) return false;
		if (ship.speed > 0.1f && ship.speed < -0.1) return false;
		return true;
	}
	
	public static MiningPlayerShip getPlayerShip(MiningGameState state, int playerId) {
		MiningPlayerShip ship = null;
		for (MiningPlayerShip currentShip : state.entityData.playerShips) {
			if (currentShip.ownerId == playerId) {
				ship = currentShip;
				break;
			}
		}
		return ship;
	}
	
	public static boolean shipIsInRecallArea(MiningGameState state, MiningPlayerShip ship) {
		return ship.position.dst(0, 0) <= state.recallRadius;
	}

}
