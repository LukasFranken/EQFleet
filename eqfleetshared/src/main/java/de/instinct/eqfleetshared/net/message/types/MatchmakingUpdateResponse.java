package de.instinct.eqfleetshared.net.message.types;

import de.instinct.eqfleetshared.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class MatchmakingUpdateResponse extends NetworkMessage {
	
	public int foundPlayers;
	public int requiredPlayers;

}
