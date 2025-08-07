package de.instinct.engine.model;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.model.planet.PlanetData;
import de.instinct.engine.model.ship.ShipData;
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

	public Player clone() {
		Player clone = new Player();
		clone.id = this.id;
		clone.teamId = this.teamId;
		clone.name = this.name;
		clone.currentCommandPoints = this.currentCommandPoints;
		clone.commandPointsGenerationSpeed = this.commandPointsGenerationSpeed;
		clone.startCommandPoints = this.startCommandPoints;
		clone.maxCommandPoints = this.maxCommandPoints;
		clone.planetData = this.planetData.clone();
		clone.ships = new ArrayList<>();
		for (ShipData ship : this.ships) {
			clone.ships.add(ship.clone());
		}
		return clone;
	}
	
}
