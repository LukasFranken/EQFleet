package de.instinct.engine.fleet.entity.projectile;

import de.instinct.engine.core.entity.projectile.Projectile;
import de.instinct.engine.fleet.entity.unit.component.data.types.WeaponType;
import lombok.ToString;

@ToString(callSuper = true)
public class FleetProjectile extends Projectile {
	
	public int originId;
	public int weaponId;
	public String originModel;
	public WeaponType weaponType;
	public double damage;
	public double aoeRadius;

	public FleetProjectile clone() {
		FleetProjectile clone = (FleetProjectile) super.clone();
		return clone;
	}
	
}
