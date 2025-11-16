package de.instinct.engine.ai.difficulty;

import de.instinct.engine.model.ship.components.HullData;
import de.instinct.engine.model.ship.components.ShieldData;
import de.instinct.engine.model.ship.components.WeaponData;
import de.instinct.engine.model.ship.components.types.ShieldType;
import de.instinct.engine.model.ship.components.types.WeaponType;

public class AiStatManager {
	
	private static float descaleFactor = 20f;

	public static float getMovementSpeed(int threatLevel) {
		float maxMoveSpeed = 100f;
		float minMoveSpeed = 40f;
		return Math.min(minMoveSpeed + minMoveSpeed * (threatLevel/100), maxMoveSpeed);
	}

	public static WeaponData getShipWeapon(int threatLevel) {
		WeaponData aiShipWeapon = new WeaponData();
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

	public static ShieldData getShipShield(int threatLevel) {
		ShieldData aiShipShield = new ShieldData();
		aiShipShield.type = ShieldType.PLASMA;
		aiShipShield.strength = 5 + 5 * (threatLevel / descaleFactor);
		aiShipShield.generation = 0.3f + 0.3f * (threatLevel / descaleFactor);
		return aiShipShield;
	}
	
	public static HullData getShipHull(int threatLevel) {
		HullData aiShipHull = new HullData();
		aiShipHull.strength = 8 + 8 * (threatLevel / descaleFactor);
		return aiShipHull;
	}

	public static WeaponData getTurretWeapon(int threatLevel) {
		WeaponData aiPlanetWeapon = new WeaponData();
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
	
	public static ShieldData getTurretShield(int threatLevel) {
		ShieldData aiTurretShield = new ShieldData();
		aiTurretShield.type = ShieldType.PLASMA;
		aiTurretShield.strength = 15 + 15 * (threatLevel / descaleFactor);
		aiTurretShield.generation = 0.5f + 0.5f * (threatLevel / descaleFactor);
		return aiTurretShield;
	}
	
	public static HullData getTurretHull(int threatLevel) {
		HullData aiShipHull = new HullData();
		aiShipHull.strength = 20 + 20 * (threatLevel / descaleFactor);
		return aiShipHull;
	}

	public static float getResourceGenerationSpeed(int threatLevel) {
		float resourceGenerationSpeedPerPowOfTen = 1f;
		return scaleWithThreatLevelByPowerOfTen(threatLevel, resourceGenerationSpeedPerPowOfTen);
	}

	public static float getMaxResourceCapacity(int threatLevel) {
		float maxResourceCapacityPerPowOfTen = 10f;
		return scaleWithThreatLevelByPowerOfTen(threatLevel, maxResourceCapacityPerPowOfTen);
	}

	public static double getCommandPointsGenerationSpeed(int threatLevel) {
		float commandPointsGenerationSpeedPerPowOfTen = 0.1f;
		return scaleWithThreatLevelByPowerOfTen(threatLevel, commandPointsGenerationSpeedPerPowOfTen);
	}
	
	public static double getStartCommandPoints(int threatLevel) {
		float startCommandPointsPerPowOfTen = 3f;
		return scaleWithThreatLevelByPowerOfTen(threatLevel, startCommandPointsPerPowOfTen);
	}
	
	public static double getMaxCommandPoints(int threatLevel) {
		float maxCommandPointsPerPowOfTen = 10f;
		return scaleWithThreatLevelByPowerOfTen(threatLevel, maxCommandPointsPerPowOfTen);
	}
	
	private static float scaleWithThreatLevelByPowerOfTen(int threatLevel, float baseValue) {
		float remainingThreat = threatLevel;
		float scalingMaxResourceCapacity = 0;
		while (remainingThreat > 10) {
			remainingThreat /= 10;
			scalingMaxResourceCapacity += baseValue;
		}
		scalingMaxResourceCapacity += (remainingThreat/10f) * baseValue;
		return Math.max(baseValue, scalingMaxResourceCapacity);
	}

}
