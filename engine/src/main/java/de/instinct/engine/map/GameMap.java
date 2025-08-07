package de.instinct.engine.map;

import java.util.List;

import de.instinct.engine.initialization.PlanetInitialization;
import lombok.ToString;

@ToString
public class GameMap {

	public float zoomFactor;
	public List<PlanetInitialization> planets;

}
