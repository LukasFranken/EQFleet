package de.instinct.engine.fleet.entity.unit.component;

import de.instinct.engine.fleet.entity.unit.component.data.WeaponData;
import lombok.ToString;

@ToString
public class Weapon {
	
	public int id;
	public float currentCooldown;
	public WeaponData data;

}
