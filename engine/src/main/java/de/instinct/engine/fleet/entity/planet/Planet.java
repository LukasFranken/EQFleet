package de.instinct.engine.fleet.entity.planet;

import de.instinct.engine.core.entity.Entity;
import lombok.ToString;

@ToString(callSuper = true)
public class Planet extends Entity {
	
	public boolean ancient;
	public int ownerId;
	public float resourceGenerationSpeed;
	public int turretSlotsLeft;
	
	public Planet clone() {
		Planet clone = (Planet) super.clone();
        return clone;
	}

}
