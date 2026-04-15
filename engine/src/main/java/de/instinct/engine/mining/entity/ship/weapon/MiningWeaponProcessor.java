package de.instinct.engine.mining.entity.ship.weapon;

import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.entity.projectile.MiningProjectileProcessor;
import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.entity.ship.core.MiningCoreProcessor;
import de.instinct.engine.mining.player.MiningPlayer;

public class MiningWeaponProcessor {
	
	private MiningProjectileProcessor projectileProcessor;
	private MiningCoreProcessor coreProcessor;
	
	public MiningWeaponProcessor() {
		projectileProcessor = new MiningProjectileProcessor();
		coreProcessor = new MiningCoreProcessor();
	}

	public void updateWeapons(MiningGameState state, MiningPlayer shipOwner, MiningPlayerShip ship, long progressionMS) {
		ship.weapon.currentCooldownMS = Math.max(0, ship.weapon.currentCooldownMS - progressionMS);
		if (shipOwner.shoot && ship.weapon.currentCooldownMS <= 0) {
			if (coreProcessor.useChargeFull(ship, ship.weapon.chargePerShot)) {
				shoot(state, shipOwner, ship);
			}
		}
	}

	private void shoot(MiningGameState state, MiningPlayer shipOwner, MiningPlayerShip ship) {
		ship.weapon.currentCooldownMS = ship.weapon.cooldownMS;
		projectileProcessor.createProjectile(state, shipOwner, ship);
	}

}
