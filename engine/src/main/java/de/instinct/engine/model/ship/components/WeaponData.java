package de.instinct.engine.model.ship.components;

import de.instinct.engine.model.ship.components.types.WeaponType;
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
