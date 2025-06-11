package de.instinct.engine.model.planet;

import de.instinct.engine.combat.unit.Unit;
import lombok.ToString;

@ToString
public class Planet extends Unit {
	
	public boolean ancient;
	
	public float resourceGenerationSpeed;
	public float maxResourceCapacity;
	public float currentResources;
	
	public Planet clone() {
		Planet clone = (Planet) super.clone();
        return clone;
	}

}
