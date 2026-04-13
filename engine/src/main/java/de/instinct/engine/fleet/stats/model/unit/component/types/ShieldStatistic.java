package de.instinct.engine.fleet.stats.model.unit.component.types;

import de.instinct.engine.fleet.stats.model.unit.component.ComponentStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ShieldStatistic extends ComponentStatistic {

	private double damageAbsorped;
	private int damageInstancesBlocked;
	private double shieldRecharged;

}
