package de.instinct.engine.ai.difficulty;

import de.instinct.engine.model.ship.Defense;
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
		aiShipWeapon.damage = 2 + 2 * (threatLevel / 10f);
		
		float maxRange = 100f;
		float minRange = 50f;
		aiShipWeapon.range = Math.min(minRange + minRange * (threatLevel/100), maxRange);
		
		float maxCooldown = 500;
		float minCooldown = 300;
		aiShipWeapon.cooldown = (int)Math.max(maxCooldown - maxCooldown * (threatLevel/100), minCooldown);
		
		float maxSpeed = 100;
		float minSpeed = 40;
		aiShipWeapon.speed = Math.min(minSpeed + minSpeed * (threatLevel/100), maxSpeed);
		return aiShipWeapon;
	}

	public static Defense getShipDefense(int threatLevel) {
		Defense aiShipDefense = new Defense();
		aiShipDefense.shield = 5 + 5 * (threatLevel / 10f);
		aiShipDefense.armor = 7 + 7 * (threatLevel / 10f);
		aiShipDefense.shieldRegenerationSpeed = 0.3f + 0.3f * (threatLevel / 10f);
		return aiShipDefense;
	}

	public static Weapon getPlanetWeapon(int threatLevel) {
		Weapon aiPlanetWeapon = new Weapon();
		aiPlanetWeapon.type = WeaponType.LASER;
		aiPlanetWeapon.damage = 2 + 2 * (threatLevel / 10f);
		
		float maxRange = 150f;
		float minRange = 100f;
		aiPlanetWeapon.range = Math.min(minRange + minRange * (threatLevel/100), maxRange);
		
		float maxCooldown = 1000;
		float minCooldown = 600;
		aiPlanetWeapon.cooldown = (int)Math.max(maxCooldown - maxCooldown * (threatLevel/100), minCooldown);
		
		float maxSpeed = 100;
		float minSpeed = 40;
		aiPlanetWeapon.speed = Math.min(minSpeed + minSpeed * (threatLevel/100), maxSpeed);
		return aiPlanetWeapon;
	}
	
	public static Defense getPlanetDefense(int threatLevel) {
		Defense aiPlanetDefense = new Defense();
		aiPlanetDefense.shield = 15 + 15 * (threatLevel / 10f);
		aiPlanetDefense.armor = 20 + 20 * (threatLevel / 10f);
		aiPlanetDefense.shieldRegenerationSpeed = 0.5f + 0.5f * (threatLevel / 10f);
		return aiPlanetDefense;
	}

}
