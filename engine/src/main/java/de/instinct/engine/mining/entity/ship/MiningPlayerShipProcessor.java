package de.instinct.engine.mining.entity.ship;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.entity.ship.core.MiningCore;
import de.instinct.engine.mining.entity.ship.core.MiningCoreProcessor;
import de.instinct.engine.mining.entity.ship.thruster.MiningThruster;
import de.instinct.engine.mining.entity.ship.thruster.MiningThrusterProcessor;
import de.instinct.engine.mining.entity.ship.weapon.MiningWeapon;
import de.instinct.engine.mining.entity.ship.weapon.MiningWeaponProcessor;
import de.instinct.engine.mining.net.message.InputChangedOrder;
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
		ship.radius = 20f;
		ship.direction = new Vector2(0, 1);
		
		ship.core = new MiningCore();
		ship.core.maxCharge = 10f;
		ship.core.currentCharge = ship.core.maxCharge;
		
		ship.thruster = new MiningThruster();
		ship.thruster.acceleration = 5f;
		ship.thruster.maxSpeed = 50f;
		ship.thruster.deceleration = 3f;
		ship.thruster.maxReverseSpeed = -5f;
		ship.thruster.rotationSpeed = 10f;
		ship.thruster.chargePerSecond = 0.2f;
		ship.thruster.inertiaDampening = 3f;
		
		ship.weapon = new MiningWeapon();
		ship.weapon.cooldownMS = 500;
		ship.weapon.lifetimeMS = 1000;
		ship.weapon.damage = 10f;
		ship.weapon.speed = 10f;
		ship.weapon.chargePerShot = 0.5f;
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
					}
				}
				return true;
			}
		}
		return false;
	}

	private boolean isValid(MiningGameState state, InputChangedOrder inputOrder) {
		return true;
	}

}
