package de.instinct.engine.util;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.planet.Planet;

public class EngineUtility {
	
	public static final Vector2 MAP_BOUNDS = new Vector2(1000, 2000);
	public static final float PLANET_RADIUS = 50f;
	
	public static Player getPlayer(List<Player> players, int playerId) {
		for (Player player : players) {
			if (player.id == playerId) {
				return player;
			}
		}
		throw new IllegalStateException("Player with playerId " + playerId + " not found in GameState." + players.size());
	}
	
	public static Planet getPlanet(List<Planet> planets, int planetId) {
		for (Planet planet : planets) {
			if (planet.id == planetId) {
				return planet;
			}
		}
		return null;
	}
	
	public static PlayerConnectionStatus getPlayerConnectionStatus(List<PlayerConnectionStatus> playerConnectionStatus, int playerId) {
		for (PlayerConnectionStatus connectionStatus : playerConnectionStatus) {
			if (connectionStatus.playerId == playerId) {
				return connectionStatus;
			}
		}
		return null;
	}
	
	public static Unit getUnit(GameState state, int id) {
		for (Ship ship : state.ships) {
			if (ship.id == id) {
				return ship;
			}
		}
		for (Turret turret : state.turrets) {
			if (turret.id == id) {
				return turret;
			}
		}
		System.out.println("couldnt find unit with id " + id);
		System.out.println("ships:");
		for (Ship ship : state.ships) {
			System.out.println(ship.id);
		}
		System.out.println("turrets:");
		for (Turret turret : state.turrets) {
			System.out.println(turret.id);
		}
		return null;
	}
	
	public static Turret getPlanetTurret(List<Turret> turrets, int planetId) {
		for (Turret turret : turrets) {
			if (turret.originPlanetId == planetId) {
				return turret;
			}
		}
		return null;
	}
	
	public static boolean mapHasAncient(GameState state) {
		for (Planet planet : state.planets) {
			if (planet.ancient) {
				return true;
			}
		}
		return false;
	}

}
