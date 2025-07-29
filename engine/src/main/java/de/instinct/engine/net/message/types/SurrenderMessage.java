package de.instinct.engine.net.message.types;

import de.instinct.engine.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class SurrenderMessage extends NetworkMessage {

	public String gameUUID;
	public String userUUID;
	
}
