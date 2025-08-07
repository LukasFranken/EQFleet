package de.instinct.engine.net.message.types;

import de.instinct.engine.net.message.NetworkMessage;
import lombok.ToString;

@ToString(callSuper = true)
public class GamePauseMessage extends NetworkMessage {

	public String gameUUID;
	public String userUUID;
	public boolean pause;
	public String reason;
	
}
