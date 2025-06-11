package de.instinct.engine.combat.unit;

import de.instinct.engine.entity.Entity;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.Weapon;
import lombok.ToString;

@ToString
public class Unit extends Entity {

	public Defense defense;
	public Weapon weapon;
	
	public Unit clone() {
		Unit clone = (Unit) super.clone();
		if (this.defense != null) {
			clone.defense = this.defense.clone();
		}
		if (this.weapon != null) {
			clone.weapon = this.weapon.clone();
		}
		return clone;
	}

}
