package de.instinct.engine.stats.model;

import lombok.Data;

@Data
public class PlayerStatistic {
	
	private int playerId;
	private int cpUsed;
	private float resourcesUsed;
	private float atpGained;

}