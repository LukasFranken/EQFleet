package de.instinct.engine.model.ship;

import de.instinct.engine.model.UnitData;
import de.instinct.engine.model.ship.components.CoreData;
import de.instinct.engine.model.ship.components.EngineData;
import lombok.ToString;

@ToString(callSuper = true)
public class ShipData extends UnitData {
	
	public CoreData core;
	public EngineData engine;

}
