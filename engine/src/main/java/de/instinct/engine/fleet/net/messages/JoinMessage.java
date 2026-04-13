package de.instinct.engine.fleet.net.messages;

import de.instinct.engine.core.net.NetworkMessage;
import lombok.ToString;

@ToString
public class JoinMessage extends NetworkMessage {
	
	public String playerUUID;

}
