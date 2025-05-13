package de.instinct.engine.net.message.types;

import de.instinct.engine.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class LoadedMessage extends NetworkMessage {
	
	public String playerUUID;

}
