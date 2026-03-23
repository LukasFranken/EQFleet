package de.instinct.engine.model.ship;

import de.instinct.engine.model.UnitData;
import de.instinct.engine.model.ship.components.types.CoreType;
import de.instinct.engine.model.ship.components.types.EngineType;
import lombok.ToString;

@ToString(callSuper = true)
public class ShipData extends UnitData {
	
	public CoreType coreType;
	public EngineType engineType;
	public float speed;
	public float acceleration;

}
