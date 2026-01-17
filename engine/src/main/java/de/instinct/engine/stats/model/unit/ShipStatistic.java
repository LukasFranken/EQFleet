package de.instinct.engine.stats.model.unit;

import de.instinct.engine.stats.model.unit.component.types.CoreStatistic;
import de.instinct.engine.stats.model.unit.component.types.EngineStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ShipStatistic extends UnitStatistic {
	
	private CoreStatistic coreStatistic;
	private EngineStatistic engineStatistic;

}
