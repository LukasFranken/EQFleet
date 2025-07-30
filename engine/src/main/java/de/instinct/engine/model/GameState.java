package de.instinct.engine.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.instinct.engine.combat.Ship;
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
	public List<Projectile> projectiles;
	public List<GameOrder> orders;
	public long gameTimeMS;
	public long maxGameTimeMS;
	public int winner;
	public int surrendered;
	public double atpToWin;
	public float ancientPlanetResourceDegradationFactor;
	public Map<Integer, Double> teamATPs;
	public boolean started;
	
	public long resumeCountdownMS;
	public long maxPauseMS;
	public long minPauseMS;
	public long currentPauseElapsedMS;
	public Map<Integer, Long> teamPausesMS;
	public Map<Integer, Integer> teamPausesCount;
	public int teamPause;
	public String currentPauseReason;
	
	public GameState clone() {
		GameState clone = new GameState();
		clone.gameUUID = this.gameUUID;
		clone.players = new ArrayList<>();
		for (Player player : this.players) {
			clone.players.add(player.clone());
		}
		clone.connectionStati = new ArrayList<>();
		for (PlayerConnectionStatus connectionStatus : this.connectionStati) {
			clone.connectionStati.add(connectionStatus.clone());
		}
		clone.planets = new ArrayList<>();
		for (Planet planet : this.planets) {
			clone.planets.add(planet.clone());
		}
		clone.ships = new ArrayList<>();
		for (Ship ship : this.ships) {
			clone.ships.add(ship.clone());
		}
		clone.projectiles = new ArrayList<>();
		for (Projectile projectile : this.projectiles) {
			clone.projectiles.add(projectile.clone());
		}
		clone.orders = new ArrayList<>();
		for (GameOrder gameOrder : this.orders) {
			clone.orders.add(gameOrder.clone());
		}
		clone.gameTimeMS = this.gameTimeMS;
		clone.maxGameTimeMS = this.maxGameTimeMS;
		clone.surrendered = this.surrendered;
		clone.winner = this.winner;
		clone.atpToWin = this.atpToWin;
		clone.ancientPlanetResourceDegradationFactor = this.ancientPlanetResourceDegradationFactor;
		clone.teamATPs = new HashMap<>(this.teamATPs);
		clone.started = this.started;
		clone.resumeCountdownMS = this.resumeCountdownMS;
		clone.maxPauseMS = this.maxPauseMS;
		clone.minPauseMS = this.minPauseMS;
		clone.currentPauseElapsedMS = this.currentPauseElapsedMS;
		clone.teamPausesMS = new HashMap<>(this.teamPausesMS);
		clone.teamPausesCount = new HashMap<>(this.teamPausesCount);
		clone.teamPause = this.teamPause;
		clone.currentPauseReason = this.currentPauseReason;
		return clone;
	}

}
