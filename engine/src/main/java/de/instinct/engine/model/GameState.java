package de.instinct.engine.model;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import de.instinct.engine.model.event.GameEvent;

public class GameState {
	
	public String gameUUID;
	public List<Player> players;
	public List<Planet> planets;
	public PriorityQueue<GameEvent> activeEvents;
	public long gameTimeMS;
	public long maxGameTimeMS;
	public int winner;
	public double atpToWin;
	public Map<Integer, Double> teamATPs;

}
