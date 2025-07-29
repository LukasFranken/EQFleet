package de.instinct.engine.ai.difficulty;

import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.model.ship.WeaponType;

public class AiStatManager {
	
	private static float descaleFactor = 20f;

	public static float getMovementSpeed(int threatLevel) {
		float maxMoveSpeed = 100f;
		float minMoveSpeed = 40f;
		return Math.min(minMoveSpeed + minMoveSpeed * (threatLevel/100), maxMoveSpeed);
	}

	public static Weapon getShipWeapon(int threatLevel) {
		Weapon aiShipWeapon = new Weapon();
		aiShipWeapon.type = WeaponType.LASER;
		aiShipWeapon.damage = 3 + 3 * (threatLevel / descaleFactor);
		
		float maxRange = 100f;
		float minRange = 50f;
		aiShipWeapon.range = Math.min(minRange + minRange * (threatLevel/(descaleFactor * 10f)), maxRange);
		
		float maxCooldown = 500;
		float minCooldown = 300;
		aiShipWeapon.cooldown = (int)Math.max(maxCooldown - maxCooldown * (threatLevel/(descaleFactor * 10f)), minCooldown);
		
		float maxSpeed = 100;
		float minSpeed = 40;
		aiShipWeapon.speed = Math.min(minSpeed + minSpeed * (threatLevel/(descaleFactor * 10f)), maxSpeed);
		return aiShipWeapon;
	}

	public static Defense getShipDefense(int threatLevel) {
		Defense aiShipDefense = new Defense();
		aiShipDefense.shield = 5 + 5 * (threatLevel / descaleFactor);
		aiShipDefense.armor = 8 + 8 * (threatLevel / descaleFactor);
		aiShipDefense.shieldRegenerationSpeed = 0.3f + 0.3f * (threatLevel / descaleFactor);
		return aiShipDefense;
	}

	public static Weapon getPlanetWeapon(int threatLevel) {
		Weapon aiPlanetWeapon = new Weapon();
		aiPlanetWeapon.type = WeaponType.LASER;
		aiPlanetWeapon.damage = 3 + 3 * (threatLevel / descaleFactor);
		
		float maxRange = 150f;
		float minRange = 100f;
		aiPlanetWeapon.range = Math.min(minRange + minRange * (threatLevel/(descaleFactor * 10f)), maxRange);
		
		float maxCooldown = 1000;
		float minCooldown = 600;
		aiPlanetWeapon.cooldown = (int)Math.max(maxCooldown - maxCooldown * (threatLevel/(descaleFactor * 10f)), minCooldown);
		
		float maxSpeed = 100;
		float minSpeed = 40;
		aiPlanetWeapon.speed = Math.min(minSpeed + minSpeed * (threatLevel/(descaleFactor * 10f)), maxSpeed);
		return aiPlanetWeapon;
	}
	
	public static Defense getPlanetDefense(int threatLevel) {
		Defense aiPlanetDefense = new Defense();
		aiPlanetDefense.shield = 15 + 15 * (threatLevel / descaleFactor);
		aiPlanetDefense.armor = 20 + 20 * (threatLevel / descaleFactor);
		aiPlanetDefense.shieldRegenerationSpeed = 0.5f + 0.5f * (threatLevel / descaleFactor);
		return aiPlanetDefense;
	}

}
