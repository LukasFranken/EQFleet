package de.instinct.engine.model.event.types;

import de.instinct.engine.model.event.GameEvent;
import lombok.ToString;

@ToString
public class FleetMovementEvent extends GameEvent {
	
	public int playerId;
	public int fromPlanetId;
	public int toPlanetId;
	public int value;

}
