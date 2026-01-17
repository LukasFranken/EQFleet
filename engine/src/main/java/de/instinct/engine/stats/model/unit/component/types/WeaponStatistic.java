package de.instinct.engine.stats.model.unit.component.types;

import de.instinct.engine.stats.model.unit.component.ComponentStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class WeaponStatistic extends ComponentStatistic {
	
	private float damageDealt;
	private int shotsFired;
	private int kills;

}
