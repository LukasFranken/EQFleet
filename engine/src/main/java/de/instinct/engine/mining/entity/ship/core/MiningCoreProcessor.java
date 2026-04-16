package de.instinct.engine.mining.entity.ship.core;

import com.badlogic.gdx.math.MathUtils;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.player.MiningPlayer;

public class MiningCoreProcessor {

	public void updateCore(MiningPlayer shipOwner, MiningPlayerShip ship, long progressionMS) {
		
	}

	public boolean useChargeFull(MiningPlayerShip ship, float chargeCost) {
		if (ship.core.currentCharge >= chargeCost) {
			ship.core.currentCharge = Math.max(0, ship.core.currentCharge - chargeCost);
			return true;
		}
		return false;
	}
	
	public float useChargePartial(MiningPlayerShip ship, float chargeCost) {
		ship.core.currentCharge = Math.max(0f, ship.core.currentCharge - chargeCost);
		return MathUtils.clamp(ship.core.currentCharge / chargeCost, 0f, 1f);
	}

}
