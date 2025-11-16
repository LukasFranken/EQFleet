package de.instinct.engine.model.ship.components;

import de.instinct.engine.model.ship.components.types.EngineType;
import lombok.ToString;

@ToString
public class EngineData {
	
	public EngineType type;
	public float speed;
	public float acceleration;

}
