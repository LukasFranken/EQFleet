package de.instinct.engine.mining.entity.ship.thruster;

import de.instinct.engine.core.util.VectorUtil;
import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.entity.ship.core.MiningCoreProcessor;
import de.instinct.engine.mining.player.MiningPlayer;

public class MiningThrusterProcessor {

	private MiningCoreProcessor coreProcessor;

	public MiningThrusterProcessor() {
		coreProcessor = new MiningCoreProcessor();
	}

	public void updateThruster(MiningPlayer shipOwner, MiningPlayerShip ship, long progressionMS) {
		float deltaSeconds = progressionMS / 1000f;
		updateSpeed(shipOwner, ship, deltaSeconds);
		updateRotation(shipOwner, ship, deltaSeconds);
	}

	private void updateSpeed(MiningPlayer owner, MiningPlayerShip ship, float deltaSeconds) {
	    int input = binaryAxis(owner.forward, owner.backward);
	    float rate = input >= 0 ? ship.thruster.acceleration : Math.abs(ship.thruster.deceleration);
	    ship.speed = updateAxis(ship, input, ship.speed, rate, ship.thruster.maxReverseSpeed, ship.thruster.maxSpeed, deltaSeconds);
	}

	private void updateRotation(MiningPlayer owner, MiningPlayerShip ship, float deltaSeconds) {
		int input = binaryAxis(owner.left, owner.right);
		ship.thruster.rotationSpeed = updateAxis(ship, input, ship.thruster.rotationSpeed, ship.thruster.rotationAcceleration, -ship.thruster.maxRotationSpeed, ship.thruster.maxRotationSpeed, deltaSeconds);

		if (ship.thruster.rotationSpeed != 0) {
			ship.direction = VectorUtil.getRotatedDirection(ship.direction, (float) (ship.thruster.rotationSpeed * deltaSeconds));
		}
	}

	private float updateAxis(MiningPlayerShip ship, int input, float currentSpeed, float accelRate, float minSpeed, float maxSpeed, float deltaSeconds) {
		float dampening = ship.thruster.inertiaDampening * deltaSeconds;

		if (input == 0) {
			if (currentSpeed > 0) return Math.max(currentSpeed - dampening, 0);
			if (currentSpeed < 0) return Math.min(currentSpeed + dampening, 0);
			return 0;
		}

		float chargeFactor = coreProcessor.useChargePartial(ship, ship.thruster.chargePerSecond * deltaSeconds);
		float thrust = input * accelRate * deltaSeconds * chargeFactor;

		boolean opposing = (input > 0 && currentSpeed < 0) || (input < 0 && currentSpeed > 0);
		if (opposing) {
			float aided = thrust + Math.signum(thrust) * dampening;
			float newSpeed = currentSpeed + aided;

			boolean crossedZero = (currentSpeed > 0 && newSpeed < 0) || (currentSpeed < 0 && newSpeed > 0);
			if (crossedZero) {
				float fractionToZero = Math.abs(currentSpeed) / Math.abs(aided);
				newSpeed = thrust * (1.0f - fractionToZero);
			}
			return clampfloat(newSpeed, minSpeed, maxSpeed);
		}
		return clampfloat(currentSpeed + thrust, minSpeed, maxSpeed);
	}

	private float clampfloat(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	private int binaryAxis(boolean positive, boolean negative) {
		if (positive == negative) return 0;
		return positive ? 1 : -1;
	}
}
