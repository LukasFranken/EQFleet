package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.UnitProcessor;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;

public class TurretProcessor extends UnitProcessor {
	
	public TurretProcessor() {
		super();
	}
	
	public void initializeTurrets(GameState state) {
		for (Planet planet : state.entityData.planets) {
			Player player = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
			if (!planet.ancient && player.turrets.size() > 0) {
				createTurretInstance(planet.id, 0, state, false);
			}
		}
	}

	public void updateTurrets(GameState state, long deltaTime) {
		for (Turret turret : state.entityData.turrets) {
			//TODO turn turret
			super.updateUnit(turret, state, deltaTime);
		}
		super.removeDestroyed(state.entityData.turrets.iterator());
	}

	public void createTurretInstance(int planetId, int turretId, GameState state, boolean payCost) {
		Planet planet = EngineUtility.getPlanet(state.entityData.planets, planetId);
		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
		Turret newTurret = new Turret();
		super.initializeUnit(newTurret, player.turrets.get(turretId), planetId, state, payCost);
		for (Shield shield : newTurret.shields) {
			shield.currentStrength = 0f;
		}
		newTurret.position = planet.position;
		newTurret.radius = EngineUtility.PLANET_RADIUS;
		newTurret.rotationSpeed = player.turrets.get(turretId).platform.rotationSpeed;
		state.entityData.turrets.add(newTurret);
	}

}
