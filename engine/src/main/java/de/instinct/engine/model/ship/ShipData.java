package de.instinct.engine.model.ship;

import de.instinct.engine.model.UnitData;
import lombok.ToString;

@ToString(callSuper = true)
public class ShipData extends UnitData {
	
	public ShipType type;
	public float movementSpeed;
	
	public ShipData clone() {
		ShipData clone = (ShipData)super.clone();
		clone.type = this.type;
		clone.movementSpeed = this.movementSpeed;
		return clone;
	}

}
