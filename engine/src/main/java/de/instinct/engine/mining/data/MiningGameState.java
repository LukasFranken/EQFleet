package de.instinct.engine.mining.data;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.mining.data.map.MiningMap;
import de.instinct.engine.mining.entity.data.MiningEntityData;
import lombok.ToString;

@ToString(callSuper = true)
public class MiningGameState extends GameState {
	
	public Vector2 recallPosition;
	public float recallRadius;
	public MiningEntityData entityData;
	public MiningMap map;

}
