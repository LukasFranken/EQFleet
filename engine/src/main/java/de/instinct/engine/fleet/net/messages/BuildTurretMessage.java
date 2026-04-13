package de.instinct.engine.fleet.net.messages;

import de.instinct.engine.core.net.NetworkMessage;
import lombok.ToString;

@ToString(callSuper = true)
public class BuildTurretMessage extends NetworkMessage {

	public String gameUUID;
	public String userUUID;
	public int planetId;
	
}
