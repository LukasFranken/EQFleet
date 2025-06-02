package de.instinct.engine.initialization;

import java.util.List;

import de.instinct.engine.model.Player;
import lombok.ToString;

@ToString
public class GameStateInitialization {
	
	public String gameUUID;
	public List<Player> players;
	public List<PlanetInitialization> planets;
	public float ancientPlanetResourceDegradationFactor;

}
