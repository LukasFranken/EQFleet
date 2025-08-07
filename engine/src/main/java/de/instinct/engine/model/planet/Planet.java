package de.instinct.engine.model.planet;

import de.instinct.engine.entity.Entity;
import lombok.ToString;

@ToString(callSuper = true)
public class Planet extends Entity {
	
	public boolean ancient;
	
	public float resourceGenerationSpeed;
	public float maxResourceCapacity;
	public float currentResources;
	
	public Planet clone() {
		Planet clone = (Planet) super.clone();
        return clone;
	}

}
