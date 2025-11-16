package de.instinct.engine.model;

import java.util.List;

import de.instinct.engine.model.planet.PlanetData;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.turret.TurretData;
import lombok.ToString;

@ToString(callSuper = true)
public class Player {
	
	public int id;
	public int teamId;
	public String name;
	
	public double currentCommandPoints;
	public double commandPointsGenerationSpeed;
	public double startCommandPoints;
	public double maxCommandPoints;
	
	public PlanetData planetData;
	public List<ShipData> ships;
	public List<TurretData> turrets;
	
}
