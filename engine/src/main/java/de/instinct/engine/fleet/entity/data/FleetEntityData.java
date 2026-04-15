package de.instinct.engine.fleet.entity.data;

import java.util.List;

import de.instinct.engine.core.entity.data.EntityData;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.entity.projectile.FleetProjectile;
import de.instinct.engine.fleet.entity.unit.ship.Ship;
import de.instinct.engine.fleet.entity.unit.turret.Turret;

public class FleetEntityData extends EntityData {
	
	public List<Planet> planets;
	public List<Ship> ships;
	public List<Turret> turrets;
	public List<FleetProjectile> projectiles;

}
