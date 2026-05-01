package de.instinct.engine.mining.data.map.node.types;

import de.instinct.engine.mining.data.map.node.MiningMapNode;
import de.instinct.engine.mining.entity.asteroid.ResourceType;
import lombok.ToString;

@ToString(callSuper = true)
public class AsteroidMapNode extends MiningMapNode {
	
	public float health;
	public float value;
	public ResourceType resourceType;

}
