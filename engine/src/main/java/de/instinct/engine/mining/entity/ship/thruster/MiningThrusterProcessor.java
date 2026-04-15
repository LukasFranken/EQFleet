package de.instinct.engine.mining.entity.ship.thruster;

import com.badlogic.gdx.math.MathUtils;

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
		double deltaSeconds = progressionMS / 1000f;

		updateSpeed(shipOwner, ship, deltaSeconds);
		updateRotation(shipOwner, ship, deltaSeconds);
	}

	private void updateSpeed(MiningPlayer owner, MiningPlayerShip ship, double deltaSeconds) {
	    int linearInput = binaryAxis(owner.forward, owner.backward);
	    double dampening = ship.thruster.inertiaDampening * deltaSeconds;

	    if (linearInput == 0) {
	        if (ship.speed > 0) {
	            ship.speed = Math.max(ship.speed - dampening, 0);
	        } else if (ship.speed < 0) {
	            ship.speed = Math.min(ship.speed + dampening, 0);
	        }
	        return;
	    }

	    double chargeFactor = coreProcessor.useChargePartial(ship, ship.thruster.chargePerSecond * deltaSeconds);
	    double rate = linearInput > 0 ? ship.thruster.acceleration : ship.thruster.deceleration;
	    double thrust = linearInput * rate * deltaSeconds * chargeFactor;

	    boolean thrustOpposesVelocity = (linearInput > 0 && ship.speed < 0) || (linearInput < 0 && ship.speed > 0);
	    if (thrustOpposesVelocity) {
	    	double aided = thrust + Math.signum(thrust) * dampening;
	        double oldSpeed = ship.speed;
	        double newSpeed = oldSpeed + aided;

	        boolean crossedZero = (oldSpeed > 0 && newSpeed < 0) || (oldSpeed < 0 && newSpeed > 0);
	        if (crossedZero) {
	        	double fractionToZero = Math.abs(oldSpeed) / Math.abs(aided);
	        	double remainingFraction = 1f - fractionToZero;
	            newSpeed = thrust * remainingFraction;
	        }
	        ship.speed = MathUtils.clamp(newSpeed, ship.thruster.maxReverseSpeed, ship.thruster.maxSpeed);
	    } else {
	        ship.speed = MathUtils.clamp(ship.speed + thrust, ship.thruster.maxReverseSpeed, ship.thruster.maxSpeed);
	    }
	}

	private void updateRotation(MiningPlayer owner, MiningPlayerShip ship, double deltaSeconds) {
		int rotationInput = binaryAxis(owner.left, owner.right);
		if (rotationInput == 0) return;

		ship.direction = VectorUtil.getRotatedDirection(
			ship.direction,
			(float) (rotationInput * ship.thruster.rotationSpeed * deltaSeconds)
		);
	}

	private int binaryAxis(boolean positive, boolean negative) {
		if (positive == negative) return 0;
		return positive ? 1 : -1;
	}
}
