package de.instinct.engine.stats.model.unit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShipStatistic extends UnitStatistic {
	
	private float distanceTraveled;

}
