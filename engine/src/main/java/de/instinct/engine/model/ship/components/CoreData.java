package de.instinct.engine.model.ship.components;

import de.instinct.engine.model.ship.components.types.CoreType;
import lombok.ToString;

@ToString(callSuper = true)
public class CoreData extends ComponentData {
	
	public CoreType type;

}