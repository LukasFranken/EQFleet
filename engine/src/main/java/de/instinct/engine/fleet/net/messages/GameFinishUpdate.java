package de.instinct.engine.fleet.net.messages;

import de.instinct.engine.core.net.NetworkMessage;
import de.instinct.engine.fleet.data.ResultData;
import lombok.ToString;

@ToString(callSuper = true)
public class GameFinishUpdate extends NetworkMessage {
	
	public ResultData resultData;

}
