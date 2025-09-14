package de.instinct.engine.stats.model.unit;

import lombok.Data;

@Data
public abstract class UnitStatistic {
	
	private int id;
	private float damageDealt;
	private float damageTaken;
	private int kills;
	private int timesBuilt;

}
