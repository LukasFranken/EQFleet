package de.instinct.engine.model;

import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.Weapon;
import lombok.ToString;

@ToString
public class PlanetData {
	
	public float resourceGenerationSpeed;
	public float maxResourceCapacity;
	public float percentOfArmorAfterCapture;
	public Defense defense;
	public Weapon weapon;
	
	public PlanetData clone() {
		PlanetData clone = new PlanetData();
		clone.resourceGenerationSpeed = this.resourceGenerationSpeed;
		clone.maxResourceCapacity = this.maxResourceCapacity;
		clone.percentOfArmorAfterCapture = this.percentOfArmorAfterCapture;
		if (this.defense != null) {
			clone.defense = this.defense.clone();
		}
		if (this.weapon != null) {
			clone.weapon = this.weapon.clone();
		}
		return clone;
	}

}
