package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.UnitProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;

public class TurretProcessor extends UnitProcessor {
	
	public TurretProcessor() {
		super();
	}
	
	public void initializeTurrets(GameState state) {
		for (Planet planet : state.planets) {
			Player player = EngineUtility.getPlayer(state.players, planet.ownerId);
			if (!planet.ancient && player.planetData.turret != null) {
				createTurretInstance(planet.id, state, false);
			}
		}
	}

	public void updateTurrets(GameState state, long deltaTime) {
		for (Turret turret : state.turrets) {
			//TODO turn turret
			super.updateUnit(turret, state, deltaTime);
		}
		super.removeDestroyed(state.turrets.iterator());
	}

	public void createTurretInstance(int planetId, GameState state, boolean payCost) {
		Planet planet = EngineUtility.getPlanet(state.planets, planetId);
		Player player = EngineUtility.getPlayer(state.players, planet.ownerId);
		Turret newTurret = new Turret();
		super.initializeUnit(newTurret, player.planetData.turret, planetId, state, payCost);
		newTurret.defense.currentShield = 0f;
		newTurret.position = planet.position;
		newTurret.radius = EngineUtility.PLANET_RADIUS;
		newTurret.rotationSpeed = player.planetData.turret.rotationSpeed;
		state.turrets.add(newTurret);
	}

}
