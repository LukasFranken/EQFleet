package de.instinct.eqfleetshared.net.message.types;

import de.instinct.eqfleetshared.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class FleetMovementMessage extends NetworkMessage {
	
	public String gameUUID;
	public String userUUID;
	public int fromPlanetId;
	public int toPlanetId;

}
