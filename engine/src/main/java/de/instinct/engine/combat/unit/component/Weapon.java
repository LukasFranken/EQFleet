package de.instinct.engine.combat.unit.component;

import de.instinct.engine.model.ship.components.WeaponData;
import lombok.ToString;

@ToString
public class Weapon {
	
	public int id;
	public float currentCooldown;
	public WeaponData data;

}
