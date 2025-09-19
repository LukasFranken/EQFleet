package de.instinct.engine.stats.model;

import java.util.List;

import de.instinct.engine.stats.model.unit.ShipStatistic;
import de.instinct.engine.stats.model.unit.TurretStatistic;
import lombok.Data;

@Data
public class PlayerStatistic {
	
	private int playerId;
	private int cpUsed;
	private float resourcesUsed;
	private float atpGained;
	private List<ShipStatistic> shipStatistics;
	private List<TurretStatistic> turretStatistics;

}