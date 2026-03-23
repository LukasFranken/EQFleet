package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.UnitProcessor;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.entity.EntityProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;

public class TurretProcessor {
	
	public static void initializeTurrets(GameState state) {
		for (Planet planet : state.entityData.planets) {
			Player player = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
			if (!planet.ancient && player.turrets.size() > 0) {
				createTurretInstance(planet.id, 0, state, false);
			}
		}
	}

	public static void updateTurrets(GameState state, long deltaTime) {
		for (Turret turret : state.entityData.turrets) {
			UnitProcessor.updateUnit(turret, state, deltaTime);
		}
		EntityProcessor.removeDestroyed(state.entityData.turrets.iterator());
	}

	public static void createTurretInstance(int planetId, int turretId, GameState state, boolean payCost) {
		Planet planet = EngineUtility.getPlanet(state.entityData.planets, planetId);
		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
		Turret newTurret = new Turret();
		UnitProcessor.initializeUnit(newTurret, player.turrets.get(turretId), planetId, state, payCost);
		for (Shield shield : newTurret.shields) {
			shield.currentStrength = 0f;
		}
		newTurret.position = planet.position;
		newTurret.radius = EngineUtility.PLANET_RADIUS;
		state.entityData.turrets.add(newTurret);
	}

}
