package de.instinct.engine.stats.model.unit;

import java.util.List;

import de.instinct.engine.stats.model.unit.component.types.HullStatistic;
import de.instinct.engine.stats.model.unit.component.types.ShieldStatistic;
import de.instinct.engine.stats.model.unit.component.types.WeaponStatistic;
import lombok.Data;

@Data
public abstract class UnitStatistic {
	
	private String model;
	private int timesBuilt;
	private HullStatistic hullStatistic;
	private List<ShieldStatistic> shieldStatistics;
	private List<WeaponStatistic> weaponStatistics;

}
 