package de.instinct.engine.model;
import java.util.Map;

import de.instinct.engine.model.data.EntityData;
import de.instinct.engine.model.data.OrderData;
import de.instinct.engine.model.data.PauseData;
import de.instinct.engine.model.data.ResultData;
import de.instinct.engine.model.data.StaticData;
import lombok.ToString;

@ToString(callSuper = true)
public class GameState {
	
	public String gameUUID;
	public long gameTimeMS;
	public boolean started;
	public Map<Integer, Double> teamATPs;
	public ResultData resultData;
	public EntityData entityData;
	public OrderData orderData;
	public PauseData pauseData;
	public StaticData staticData;

}
