package de.instinct.engine.fleet.net.messages;

import de.instinct.engine.core.net.NetworkMessage;
import lombok.ToString;

@ToString(callSuper = true)
public class GamePauseMessage extends NetworkMessage {

	public String gameUUID;
	public String userUUID;
	public boolean pause;
	public String reason;
	
}
