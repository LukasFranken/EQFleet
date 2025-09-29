package de.instinct.engine.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.instinct.engine.model.Player;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.stats.model.GameStatistic;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.stats.model.unit.ShipStatistic;
import de.instinct.engine.stats.model.unit.TurretStatistic;

public class StatCollector {
	
	private static Map<String, GameStatistic> statistics;
	
	public static void initialize(String gameUUID) {
		if (statistics == null) statistics = new HashMap<>();
		statistics.put(gameUUID, generateInitialStatistic());
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
			playerStatistic.getShipStatistics().add(shipStatistic);
		}
		
		playerStatistic.setTurretStatistics(new ArrayList<>());
		if (player.planetData.turret != null) {
			TurretStatistic turretStatistic = new TurretStatistic();
			turretStatistic.setModel(player.planetData.turret.model);
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
	
	public static PlayerStatistic getPlayer(String gameUUID, int playerId) {
		for (PlayerStatistic playerStatistic : get(gameUUID).getPlayerStatistics()) {
			if (playerStatistic.getPlayerId() == playerId)
				return playerStatistic;
			
		}
		return null;
	}

}
