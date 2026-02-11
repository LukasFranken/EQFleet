package de.instinct.engine.model;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.order.GameOrder;
import lombok.ToString;

@ToString(callSuper = true)
public class GameState {
	
	public String gameUUID;
	public long gameTimeMS;
	public boolean started;
	public int winner;
	public int surrendered;
	public Map<Integer, Double> teamATPs;
	
	public long maxGameTimeMS;
	public float zoomFactor;
	public double atpToWin;
	public float ancientPlanetResourceDegradationFactor;
	
	public int orderCounter;
	public Queue<GameOrder> unprocessedOrders;
	public List<GameOrder> orders;
	
	public List<Player> players;
	public List<PlayerConnectionStatus> connectionStati;
	
	public int entityCounter;
	public List<Planet> planets;
	public List<Ship> ships;
	public List<Turret> turrets;
	public List<Projectile> projectiles;
	
	public PauseData pauseData;

}
