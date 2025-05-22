package de.instinct.engine.model;

import java.util.List;

import de.instinct.engine.model.ship.Ship;
import lombok.ToString;

@ToString
public class Player {
	
	public String name;
	public boolean loaded;
	public boolean connected;
	public int teamId;
	public int playerId;
	
	public double resourceGenerationSpeed;
	public double maxPlanetCapacity;
	
	public double currentCommandPoints;
	public double commandPointsGenerationSpeed;
	public double startCommandPoints;
	public double maxCommandPoints;
	
	public List<Ship> ships;

}
