package de.instinct.eqfleetshared.net.message.types;

import de.instinct.eqfleetshared.net.message.NetworkMessage;
import lombok.ToString;

@ToString
public class PlayerAssigned extends NetworkMessage {
	
	public int factionId;

}
