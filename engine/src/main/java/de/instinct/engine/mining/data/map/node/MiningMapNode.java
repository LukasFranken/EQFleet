package de.instinct.engine.mining.data.map.node;

import com.badlogic.gdx.math.Vector2;

import lombok.ToString;

@ToString
public abstract class MiningMapNode {
	
	public Vector2 position;
	public float radius;

}
