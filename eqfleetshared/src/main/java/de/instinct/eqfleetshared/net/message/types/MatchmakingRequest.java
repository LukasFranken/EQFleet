package de.instinct.eqfleetshared.net.message.types;

import de.instinct.eqfleetshared.net.enums.FactionMode;
import de.instinct.eqfleetshared.net.enums.GameMode;
import de.instinct.eqfleetshared.net.enums.VersusMode;
import de.instinct.eqfleetshared.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class MatchmakingRequest extends NetworkMessage {
	
	public String playerUUID;
	public VersusMode versusMode;
	public GameMode gameMode;
	public FactionMode factionMode;

}
