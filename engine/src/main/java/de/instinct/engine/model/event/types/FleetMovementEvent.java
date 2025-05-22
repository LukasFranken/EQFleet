package de.instinct.engine.model.event.types;

import de.instinct.engine.model.event.GameEvent;
import de.instinct.engine.model.ship.Ship;
import lombok.ToString;

@ToString
public class FleetMovementEvent extends GameEvent {
	
	public int playerId;
	public int fromPlanetId;
	public int toPlanetId;
	public Ship shipData;

}
