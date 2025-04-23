package de.instinct.eqfleetshared.gamelogic.event.model.subtypes;

import de.instinct.eqfleetshared.gamelogic.event.model.GameEvent;
import lombok.ToString;

@ToString
public class FleetMovementEvent extends GameEvent {
	
	public int playerId;
	public int fromPlanetId;
	public int toPlanetId;
	public int value;

}
