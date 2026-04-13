package de.instinct.engine.fleet.entity.unit.ship.data;

import de.instinct.engine.fleet.entity.unit.data.UnitData;
import de.instinct.engine.fleet.entity.unit.ship.component.types.CoreType;
import de.instinct.engine.fleet.entity.unit.ship.component.types.EngineType;
import lombok.ToString;

@ToString(callSuper = true)
public class ShipData extends UnitData {
	
	public CoreType coreType;
	public EngineType engineType;
	public float speed;
	public float acceleration;
	public float transferRate;

}
