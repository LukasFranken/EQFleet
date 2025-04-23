package de.instinct.eqfleetshared.gamelogic.model;

import java.util.List;
import java.util.PriorityQueue;

import de.instinct.eqfleetshared.gamelogic.event.model.GameEvent;

public class GameState {
	
	public String gameUUID;
	public List<Player> players;
	public List<Planet> planets;
	public PriorityQueue<GameEvent> activeEvents;
	public long gameTimeMS;
	public long maxGameTimeMS;
	public int winner;
	public double atpToWin;

}
