package de.instinct.engine.ai.difficulty;

import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.model.ship.WeaponType;

public class AiStatManager {

	public static float getMovementSpeed(int threatLevel) {
		float maxMoveSpeed = 100f;
		float minMoveSpeed = 40f;
		return Math.min(minMoveSpeed + minMoveSpeed * (threatLevel/100), maxMoveSpeed);
	}

	public static Weapon getShipWeapon(int threatLevel) {
		Weapon aiShipWeapon = new Weapon();
		aiShipWeapon.type = WeaponType.LASER;
		aiShipWeapon.damage = 4;
		aiShipWeapon.range = 60f;
		aiShipWeapon.cooldown = 500;
		aiShipWeapon.speed = 100f;
		return aiShipWeapon;
	}

}
