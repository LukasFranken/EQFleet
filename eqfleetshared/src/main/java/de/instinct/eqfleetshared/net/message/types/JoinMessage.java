package de.instinct.eqfleetshared.net.message.types;

import de.instinct.eqfleetshared.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class JoinMessage extends NetworkMessage {
	
	public String playerUUID;
	public String lobbyUUID;

}
