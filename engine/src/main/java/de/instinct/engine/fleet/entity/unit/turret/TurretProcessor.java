package de.instinct.engine.fleet.entity.unit.turret;

import java.util.List;

import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.entity.planet.PlanetProcessor;
import de.instinct.engine.fleet.entity.unit.UnitProcessor;
import de.instinct.engine.fleet.entity.unit.component.Shield;
import de.instinct.engine.fleet.entity.unit.component.Weapon;
import de.instinct.engine.fleet.entity.unit.turret.data.TurretData;
import de.instinct.engine.fleet.player.FleetPlayer;
import de.instinct.engine.fleet.player.FleetPlayerProcessor;

public class TurretProcessor {
	
	private UnitProcessor unitProcessor;
	private FleetPlayerProcessor playerProcessor;
	
	public TurretProcessor() {
		unitProcessor = new UnitProcessor();
		playerProcessor = new FleetPlayerProcessor();
	}

	public void updateTurrets(FleetGameState state, long deltaTime) {
		for (Turret turret : state.entityData.turrets) {
			unitProcessor.updateUnit(turret, state, deltaTime);
		}
		unitProcessor.removeDestroyed(state.entityData.turrets.iterator());
	}
	
	public void createTurret(Planet planet, int turretDataId, FleetGameState state, boolean payCost) {
		if (planet == null) return;
		if (planet.turretSlotsLeft <= 0) return;
		
		FleetPlayer player = playerProcessor.getFleetPlayer(state.playerData.players, planet.ownerId);
		TurretData turretData = player.turrets.get(turretDataId);
		
		Turret turret = getPlanetTurret(state.entityData.turrets, planet.id);
		if (turret == null) {
			Turret newTurret = new Turret();
			unitProcessor.initializeUnit(newTurret, turretData, planet, state, payCost);
			for (Shield shield : newTurret.shields) {
				shield.currentStrength = 0f;
			}
			newTurret.position = planet.position;
			newTurret.radius = PlanetProcessor.PLANET_RADIUS;
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
	
	public Turret getPlanetTurret(List<Turret> turrets, int planetId) {
		for (Turret turret : turrets) {
			if (turret.originPlanetId == planetId) {
				return turret;
			}
		}
		return null;
	}

}
