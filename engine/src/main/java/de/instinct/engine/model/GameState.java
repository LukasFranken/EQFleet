package de.instinct.engine.model;

import java.util.List;
import java.util.Map;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.order.GameOrder;
import lombok.ToString;

@ToString
public class GameState {
	
	public String gameUUID;
	public List<Player> players;
	public List<PlayerConnectionStatus> connectionStati;
	public List<Planet> planets;
	public List<Ship> ships;
	public List<Projectile> projectiles;
	public List<GameOrder> orders;
	public long gameTimeMS;
	public long maxGameTimeMS;
	public int winner;
	public double atpToWin;
	public float ancientPlanetResourceDegradationFactor;
	public Map<Integer, Double> teamATPs;
	public boolean started;

}
