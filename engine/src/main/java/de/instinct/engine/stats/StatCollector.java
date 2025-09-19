package de.instinct.engine.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	
	public static void initializePlayer(String gameUUID, int playerId, int ships, int turrets) {
		GameStatistic initStatistic = statistics.get(gameUUID);
		PlayerStatistic playerStatistic = new PlayerStatistic();
		playerStatistic.setPlayerId(playerId);
		
		playerStatistic.setShipStatistics(new ArrayList<>());
		for (int i = 0; i < ships; i++) {
			ShipStatistic shipStatistic = new ShipStatistic();
			shipStatistic.setId(i);
			playerStatistic.getShipStatistics().add(shipStatistic);
		}
		
		playerStatistic.setTurretStatistics(new ArrayList<>());
		for (int i = 0; i < turrets; i++) {
			TurretStatistic turretStatistic = new TurretStatistic();
			turretStatistic.setId(i);
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

}
