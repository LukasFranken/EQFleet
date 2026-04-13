package de.instinct.engine.fleet.net.messages;

import de.instinct.engine.core.net.NetworkMessage;
import lombok.ToString;

@ToString
public class SurrenderMessage extends NetworkMessage {

	public String gameUUID;
	public String userUUID;
	
}
