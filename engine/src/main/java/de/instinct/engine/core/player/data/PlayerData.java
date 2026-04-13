package de.instinct.engine.core.player.data;

import java.util.List;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.net.data.PlayerConnectionStatus;
import lombok.ToString;

@ToString
public class PlayerData {
	
	public List<Player> players;
	public List<PlayerConnectionStatus> connectionStati;

}
