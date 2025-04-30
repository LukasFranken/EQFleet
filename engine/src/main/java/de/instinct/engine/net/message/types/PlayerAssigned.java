package de.instinct.engine.net.message.types;

import de.instinct.engine.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class PlayerAssigned extends NetworkMessage {
	
	public int playerId;

}
