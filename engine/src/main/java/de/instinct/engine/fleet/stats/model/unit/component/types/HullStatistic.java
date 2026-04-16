package de.instinct.engine.fleet.stats.model.unit.component.types;

import de.instinct.engine.fleet.stats.model.unit.component.ComponentStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class HullStatistic extends ComponentStatistic {

	private float damageTaken;
	private float hullRepaired;

}
