package de.instinct.engine.model.ship;

import lombok.ToString;

@ToString
public class ShipData {
	
	public String model;
	public ShipType type;
	public float movementSpeed;
	public int cost;
	public int commandPointsCost;
	public Defense defense;
	public Weapon weapon;
	
	public ShipData clone() {
		ShipData clone = new ShipData();
		clone.model = this.model;
		clone.type = this.type;
		clone.movementSpeed = this.movementSpeed;
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
