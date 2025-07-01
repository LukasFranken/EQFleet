package de.instinct.engine.net.message.types;

import de.instinct.engine.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class FleetMovementMessage extends NetworkMessage {
	
	public String gameUUID;
	public String userUUID;
	public int fromPlanetId;
	public int toPlanetId;
	public int shipId;

}
