package de.instinct.engine.mining.net.message;

import de.instinct.engine.core.net.NetworkMessage;
import de.instinct.engine.mining.data.MiningGameState;
import lombok.ToString;

@ToString
public class OnboardMessage extends NetworkMessage {
	
	public int assignedPlayerId;
	public MiningGameState initialGameState;

}
