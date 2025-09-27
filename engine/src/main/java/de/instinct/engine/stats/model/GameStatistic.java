package de.instinct.engine.stats.model;

import java.util.List;

import lombok.Data;

@Data
public class GameStatistic {
	
	private List<PlayerStatistic> playerStatistics;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-----------------------------\n");
		sb.append("GAME STATISTICS:\n");
		sb.append("-----------------------------\n");
		sb.append("PLAYERS\n");
		sb.append("-----------------------------\n");
		for (PlayerStatistic ps : playerStatistics) {
			sb.append(ps.toString()).append("\n");
		}
		sb.append("-----------------------------\n");
		return sb.toString();
	}

}
