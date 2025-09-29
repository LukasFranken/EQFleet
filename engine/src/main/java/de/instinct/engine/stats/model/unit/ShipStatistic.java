package de.instinct.engine.stats.model.unit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ShipStatistic extends UnitStatistic {
	
	private float distanceTraveled;

}
