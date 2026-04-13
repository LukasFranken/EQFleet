package de.instinct.engine.fleet.data;
import java.util.Map;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.fleet.entity.data.EntityData;
import lombok.ToString;

@ToString(callSuper = true)
public class FleetGameState extends GameState {
	
	public Map<Integer, Double> teamATPs;
	public ResultData resultData;
	public EntityData entityData;
	public StaticData staticData;

}
