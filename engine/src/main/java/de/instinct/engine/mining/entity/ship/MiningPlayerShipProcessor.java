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
import de.instinct.engine.mining.net.message.InputChangedOrder;
import de.instinct.engine.mining.net.message.RecallOrder;
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

	public void createPlayerShip(MiningGameState state, int ownerId) {
		MiningPlayerShip ship = new MiningPlayerShip();
		super.initializeEntity(ship, state.entityData);
		ship.ownerId = ownerId;
		ship.position.set(0, 0);
		ship.radius = 10f;
		ship.direction = new Vector2(0, 1);
		
		ship.core = new MiningCore();
		ship.core.maxCharge = 20f;
		ship.core.currentCharge = ship.core.maxCharge;
		
		ship.thruster = new MiningThruster();
		ship.thruster.acceleration = 20f;
		ship.thruster.maxSpeed = 200f;
		ship.thruster.deceleration = 15f;
		ship.thruster.maxReverseSpeed = -30f;
		ship.thruster.rotationAcceleration = 20f;
		ship.thruster.maxRotationSpeed = 50f;
		ship.thruster.chargePerSecond = 0.2f;
		ship.thruster.inertiaDampening = 50f;
		
		ship.weapon = new MiningWeapon();
		ship.weapon.cooldownMS = 500;
		ship.weapon.lifetimeMS = 2000;
		ship.weapon.damage = 5f;
		ship.weapon.speed = 10f;
		ship.weapon.chargePerShot = 0.5f;
		
		ship.cargo = new MiningCargo();
		ship.cargo.capacity = 20f;
		ship.cargo.items = new ArrayList<>();
		state.entityData.playerShips.add(ship);
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
		if (ship.speed > 0.1f) return false;
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
