package de.instinct.engine.fleet.data;
import java.util.Map;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.fleet.entity.data.FleetEntityData;
import lombok.ToString;

@ToString(callSuper = true)
public class FleetGameState extends GameState {
	
	public Map<Integer, Float> teamATPs;
	public ResultData resultData;
	public FleetEntityData entityData;
	public StaticData staticData;

}
