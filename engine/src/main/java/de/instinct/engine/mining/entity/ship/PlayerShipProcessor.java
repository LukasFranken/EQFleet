package de.instinct.engine.mining.entity.ship;

import com.badlogic.gdx.math.MathUtils;

import de.instinct.engine.core.entity.EntityProcessor;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.core.util.VectorUtil;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.net.message.InputChangedOrder;
import de.instinct.engine.mining.player.MiningPlayer;
import de.instinct.engine.mining.player.MiningPlayerProcessor;

public class PlayerShipProcessor extends EntityProcessor {
	
	private MiningPlayerProcessor playerProcessor;
	
	public PlayerShipProcessor() {
		playerProcessor = new MiningPlayerProcessor();
	}

	public void update(MiningGameState state, long progressionMS) {
		for (PlayerShip ship : state.playerShips) {
			updateShip(state, ship, progressionMS);
		}
	}

	private void updateShip(MiningGameState state, PlayerShip ship, long progressionMS) {
		super.updateEntity(ship, state, progressionMS);
		MiningPlayer shipOwner = playerProcessor.getMiningPlayer(state.playerData.players, ship.ownerId);
		if (shipOwner.forward && !shipOwner.backward) {
			ship.speed = MathUtils.clamp(ship.speed + (ship.acceleration * (float)progressionMS / 1000f), ship.maxReverseSpeed, ship.maxSpeed);
		}
		if (shipOwner.backward && !shipOwner.forward) {
			ship.speed = MathUtils.clamp(ship.speed - (ship.deceleration * (float)progressionMS / 1000f), ship.maxReverseSpeed, ship.maxSpeed);
		}
		if (shipOwner.left && !shipOwner.right) {
			ship.direction = VectorUtil.getRotatedDirection(ship.direction, ship.rotationSpeed * (float)progressionMS / 1000f);
		}
		if (shipOwner.right && !shipOwner.left) {
			ship.direction = VectorUtil.getRotatedDirection(ship.direction, -ship.rotationSpeed * (float)progressionMS / 1000f);
		}
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
