package de.instinct.engine.mining.entity.ship.core;

import com.badlogic.gdx.math.MathUtils;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.player.MiningPlayer;

public class MiningCoreProcessor {

	public void updateCore(MiningPlayer shipOwner, MiningPlayerShip ship, long progressionMS) {
		
	}

	public boolean useChargeFull(MiningPlayerShip ship, double chargeCost) {
		if (ship.core.currentCharge >= chargeCost) {
			ship.core.currentCharge = Math.max(0, ship.core.currentCharge - chargeCost);
			return true;
		}
		return false;
	}
	
	public double useChargePartial(MiningPlayerShip ship, double chargeCost) {
		ship.core.currentCharge = Math.max(0f, ship.core.currentCharge - chargeCost);
		return MathUtils.clamp(ship.core.currentCharge / chargeCost, 0f, 1f);
	}

}
