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
	public List<Player> players;
	public List<PlayerConnectionStatus> connectionStati;
	public List<Planet> planets;
	public List<Ship> ships;
	public List<Turret> turrets;
	public List<Projectile> projectiles;
	public Queue<GameOrder> unprocessedOrders;
	public List<GameOrder> orders;
	public long gameTimeMS;
	public long maxGameTimeMS;
	public int winner;
	public int surrendered;
	public double atpToWin;
	public float ancientPlanetResourceDegradationFactor;
	public Map<Integer, Double> teamATPs;
	public boolean started;
	public int entityCounter;
	public int orderCounter;
	public float zoomFactor;
	
	public long resumeCountdownMS;
	public long maxPauseMS;
	public long minPauseMS;
	public long currentPauseElapsedMS;
	public Map<Integer, Long> teamPausesMS;
	public Map<Integer, Integer> teamPausesCount;
	public int teamPause;
	public String currentPauseReason;

}
