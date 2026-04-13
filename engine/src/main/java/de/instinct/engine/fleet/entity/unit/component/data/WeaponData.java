package de.instinct.engine.fleet.entity.unit.component.data;

import de.instinct.engine.fleet.entity.unit.component.data.types.WeaponType;
import lombok.ToString;

@ToString(callSuper = true)
public class WeaponData extends ComponentData {
	
	public WeaponType type;
	public double damage;
	public double aoeRadius;
	public double range;
	public double speed;
	public long cooldown;

}
