package de.instinct.engine.net.message.types;

import de.instinct.engine.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class JoinMessage extends NetworkMessage {
	
	public String playerUUID;
	public String lobbyUUID;

}
