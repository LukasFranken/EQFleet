package de.instinct.engine.fleet.net.messages;

import de.instinct.engine.core.net.NetworkMessage;
import lombok.ToString;

@ToString
public class LoadedMessage extends NetworkMessage {
	
	public String playerUUID;

}
