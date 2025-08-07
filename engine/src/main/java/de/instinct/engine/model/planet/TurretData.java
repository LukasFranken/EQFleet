package de.instinct.engine.model.planet;

import de.instinct.engine.model.UnitData;
import lombok.ToString;

@ToString(callSuper = true)
public class TurretData extends UnitData {
	
	public float rotationSpeed;
	
	public TurretData clone() {
		TurretData clone = (TurretData)super.clone();
		clone.rotationSpeed = this.rotationSpeed;
		return clone;
	}

}
