package de.instinct.engine.net.message.types;

import de.instinct.engine.net.message.NetworkMessage;
import lombok.ToString;

@ToString(callSuper = true)
public class BuildTurretMessage extends NetworkMessage {

	public String gameUUID;
	public String userUUID;
	public int planetId;
	
}
