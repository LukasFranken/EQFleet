package de.instinct.engine.net.message.types;

import de.instinct.engine.model.data.ResultData;
import de.instinct.engine.net.message.NetworkMessage;
import lombok.ToString;

@ToString(callSuper = true)
public class GameFinishUpdate extends NetworkMessage {
	
	public ResultData resultData;

}
