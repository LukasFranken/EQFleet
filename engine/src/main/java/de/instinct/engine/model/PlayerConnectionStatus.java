package de.instinct.engine.model;

import lombok.ToString;

@ToString
public class PlayerConnectionStatus {
	
	public int playerId;
	public boolean loaded;
	public boolean connected;

}
