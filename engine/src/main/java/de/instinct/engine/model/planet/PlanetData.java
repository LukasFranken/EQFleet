package de.instinct.engine.model.planet;

import lombok.ToString;

@ToString
public class PlanetData {
	
	public float resourceGenerationSpeed;
	public float maxResourceCapacity;
	public TurretData turret;
	
	public PlanetData clone() {
		PlanetData clone = new PlanetData();
		clone.resourceGenerationSpeed = this.resourceGenerationSpeed;
		clone.maxResourceCapacity = this.maxResourceCapacity;
		if (this.turret != null) {
			clone.turret = this.turret.clone();
		}
		return clone;
	}

}
