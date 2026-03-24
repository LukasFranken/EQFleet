package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.UnitProcessor;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.combat.unit.component.Weapon;
import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.turret.TurretData;
import de.instinct.engine.util.EngineUtility;

public class TurretProcessor {

	public static void updateTurrets(GameState state, long deltaTime) {
		for (Turret turret : state.entityData.turrets) {
			UnitProcessor.updateUnit(turret, state, deltaTime);
		}
		EntityProcessor.removeDestroyed(state.entityData.turrets.iterator());
	}

	public static void createTurret(int planetId, int turretDataId, GameState state, boolean payCost) {
		Planet planet = EngineUtility.getPlanet(state.entityData.planets, planetId);
		if (planet != null) {
			createTurret(planet, turretDataId, state, payCost);
		}
	}
	
	public static void createTurret(Planet planet, int turretDataId, GameState state, boolean payCost) {
		if (planet == null) return;
		if (planet.turretSlotsLeft <= 0) return;
		
		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
		TurretData turretData = player.turrets.get(turretDataId);
		
		Turret turret = EngineUtility.getPlanetTurret(state.entityData.turrets, planet.id);
		if (turret == null) {
			Turret newTurret = new Turret();
			UnitProcessor.initializeUnit(newTurret, turretData, planet.id, state, payCost);
			for (Shield shield : newTurret.shields) {
				shield.currentStrength = 0f;
			}
			newTurret.position = planet.position;
			newTurret.radius = EngineUtility.PLANET_RADIUS;
			state.entityData.turrets.add(newTurret);
		} else {
			turret.currentHull += turretData.hullStrength;
			turret.data.hullStrength += turretData.hullStrength;
			
			turret.data.weapons.add(turretData.weapons.get(0));
			Weapon weapon = new Weapon();
			weapon.id = turretData.weapons.get(0).id;
			weapon.currentCooldown = 0f;
			turret.weapons.add(weapon);
			
			turret.data.shields.add(turretData.shields.get(0));
			Shield shield = new Shield();
			shield.id = turretData.shields.get(0).id;
			shield.currentStrength = 0f;
			turret.shields.add(shield);
		}
		planet.turretSlotsLeft--;
	}

}
