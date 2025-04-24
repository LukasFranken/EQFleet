package de.instinct.eqfleetshared.gamelogic.order.model.subtypes;

import de.instinct.eqfleetshared.gamelogic.order.model.GameOrder;

public class FleetMovementOrder extends GameOrder {
	
	public int factionId;
	public int fromPlanetId;
	public int toPlanetId;

}
