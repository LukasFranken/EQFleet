package de.instinct.engine.fleet.entity.unit.component.data;

import de.instinct.engine.fleet.entity.unit.component.data.types.WeaponType;
import lombok.ToString;

@ToString(callSuper = true)
public class WeaponData extends ComponentData {
	
	public WeaponType type;
	public float damage;
	public float aoeRadius;
	public float range;
	public float speed;
	public long cooldown;

}
