package de.instinct.engine.model.ship.components;

import de.instinct.engine.model.ship.components.types.WeaponType;
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
