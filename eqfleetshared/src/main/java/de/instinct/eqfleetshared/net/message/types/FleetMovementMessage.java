package de.instinct.eqfleetshared.net.message.types;

import de.instinct.eqfleetshared.net.message.NetworkMessage;

public class FleetMovementMessage extends NetworkMessage {
	
	public String gameUUID;
	public int playerId;
	public int fromPlanetId;
	public int toPlanetId;

}
