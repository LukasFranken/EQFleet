package de.instinct.engine.model;

import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.Weapon;
import lombok.ToString;

@ToString(callSuper = true)
public class UnitData {
	
	public String model;
	public int cost;
	public int commandPointsCost;
	public Defense defense;
	public Weapon weapon;
	
	public UnitData clone() {
		UnitData clone = new UnitData();
		clone.model = this.model;
		clone.cost = this.cost;
		clone.commandPointsCost = this.commandPointsCost;
		if (this.defense != null) {
			clone.defense = this.defense.clone();
		}
		if (this.weapon != null) {
			clone.weapon = this.weapon.clone();
		}
		return clone;
	}

}
