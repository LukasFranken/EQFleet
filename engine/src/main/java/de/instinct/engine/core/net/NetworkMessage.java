package de.instinct.engine.core.net;

import lombok.ToString;

@ToString
public abstract class NetworkMessage {
	
	public String gameUUID;
	public String playerUUID;

}
