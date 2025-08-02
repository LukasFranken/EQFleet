package de.instinct.engine.initialization;

import java.util.List;

import de.instinct.engine.map.GameMap;
import de.instinct.engine.model.Player;
import lombok.ToString;

@ToString
public class GameStateInitialization {
	
	public String gameUUID;
	public GameMap map;
	public List<Player> players;
	public float ancientPlanetResourceDegradationFactor;
	public int gameTimeLimitMS;
	public int atpToWin;
	public long pauseTimeLimitMS;
	public int pauseCountLimit;

}