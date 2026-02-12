package de.instinct.engine.model.data;

import java.util.List;

import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import lombok.ToString;

@ToString
public class PlayerData {
	
	public List<Player> players;
	public List<PlayerConnectionStatus> connectionStati;

}
