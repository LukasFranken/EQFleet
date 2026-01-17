package de.instinct.engine.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.instinct.engine.model.Player;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.components.ShieldData;
import de.instinct.engine.model.ship.components.WeaponData;
import de.instinct.engine.model.turret.TurretData;
import de.instinct.engine.stats.model.GameStatistic;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.stats.model.unit.ShipStatistic;
import de.instinct.engine.stats.model.unit.TurretStatistic;
import de.instinct.engine.stats.model.unit.component.types.CoreStatistic;
import de.instinct.engine.stats.model.unit.component.types.EngineStatistic;
import de.instinct.engine.stats.model.unit.component.types.HullStatistic;
import de.instinct.engine.stats.model.unit.component.types.ShieldStatistic;
import de.instinct.engine.stats.model.unit.component.types.WeaponStatistic;

public class StatCollector {
	
	private static Map<String, GameStatistic> statistics;
	
	public static void initialize(String gameUUID, List<Player> players) {
		if (statistics == null) statistics = new HashMap<>();
		statistics.put(gameUUID, generateInitialStatistic());
		for (Player player : players) {
			StatCollector.initializePlayer(gameUUID, player);
		}
	}
	
	private static GameStatistic generateInitialStatistic() {
		GameStatistic initStatistic = new GameStatistic();
		initStatistic.setPlayerStatistics(new ArrayList<>());
		return initStatistic;
	}
	
	public static void initializePlayer(String gameUUID, Player player) {
		GameStatistic initStatistic = statistics.get(gameUUID);
		PlayerStatistic playerStatistic = new PlayerStatistic();
		playerStatistic.setPlayerId(player.id);
		
		playerStatistic.setShipStatistics(new ArrayList<>());
		for (ShipData ship : player.ships) {
			ShipStatistic shipStatistic = new ShipStatistic();
			shipStatistic.setModel(ship.model);
			
			CoreStatistic newCoreStatistic = new CoreStatistic();
			newCoreStatistic.setId(ship.core.id);
			shipStatistic.setCoreStatistic(newCoreStatistic);
			
			EngineStatistic newEngineStatistic = new EngineStatistic();
			newEngineStatistic.setId(ship.engine.id);
			shipStatistic.setEngineStatistic(newEngineStatistic);
			
			HullStatistic newHullStatistic = new HullStatistic();
			newHullStatistic.setId(ship.hull.id);
			shipStatistic.setHullStatistic(newHullStatistic);
			
			shipStatistic.setShieldStatistics(new ArrayList<>());
			for (ShieldData shield : ship.shields) {
				ShieldStatistic newShieldStatistic = new ShieldStatistic();
				newShieldStatistic.setId(shield.id);
				shipStatistic.getShieldStatistics().add(newShieldStatistic);
			}
			
			shipStatistic.setWeaponStatistics(new ArrayList<>());
			for (WeaponData weapon : ship.weapons) {
				WeaponStatistic newWeaponStatistic = new WeaponStatistic();
				newWeaponStatistic.setId(weapon.id);
				shipStatistic.getWeaponStatistics().add(newWeaponStatistic);
			}
			
			playerStatistic.getShipStatistics().add(shipStatistic);
		}
		
		playerStatistic.setTurretStatistics(new ArrayList<>());
		for (TurretData turret : player.turrets) {
			TurretStatistic turretStatistic = new TurretStatistic();
			turretStatistic.setModel(turret.model);
			playerStatistic.getTurretStatistics().add(turretStatistic);
		}
		
		initStatistic.getPlayerStatistics().add(playerStatistic);
	}

	public static GameStatistic get(String gameUUID) {
		return statistics.get(gameUUID);
	}
	
	public static GameStatistic grab(String gameUUID) {
		return statistics.remove(gameUUID);
	}
	
	public static boolean isInitialized(String gameUUID) {
		return statistics != null && statistics.containsKey(gameUUID);
	}
	
	public static PlayerStatistic getPlayer(String gameUUID, int playerId) {
		for (PlayerStatistic playerStatistic : get(gameUUID).getPlayerStatistics()) {
			if (playerStatistic.getPlayerId() == playerId)
				return playerStatistic;
		}
		return null;
	}

}
