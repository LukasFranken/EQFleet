package de.instinct.engine.core.player.data;

import lombok.ToString;

@ToString
public class PlayerConnectionStatus {
	
	public int playerId;
	public boolean loaded;
	public boolean connected;
	
	public PlayerConnectionStatus clone() {
		PlayerConnectionStatus clone = new PlayerConnectionStatus();
		clone.playerId = this.playerId;
		clone.loaded = this.loaded;
		clone.connected = this.connected;
		return clone;
	}

}
