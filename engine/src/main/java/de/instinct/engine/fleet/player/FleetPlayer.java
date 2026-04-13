package de.instinct.engine.fleet.player;

import java.util.List;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.entity.planet.data.PlanetData;
import de.instinct.engine.fleet.entity.unit.ship.data.ShipData;
import de.instinct.engine.fleet.entity.unit.turret.data.TurretData;
import lombok.ToString;

@ToString(callSuper = true)
public class FleetPlayer extends Player {
	
	public double currentResources;
	public double resourceGenerationSpeed;
	public double startResources;
	public double maxResources;
	public PlanetData planetData;
	public List<ShipData> ships;
	public List<TurretData> turrets;

}
