package de.instinct.engine.core.player;

import lombok.ToString;

@ToString(callSuper = true)
public abstract class Player {
	
	public int id;
	public String name;
	public int teamId;
	
}
