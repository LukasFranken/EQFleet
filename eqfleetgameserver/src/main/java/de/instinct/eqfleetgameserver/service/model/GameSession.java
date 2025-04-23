package de.instinct.eqfleetgameserver.service.model;

import java.util.List;

import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.gamelogic.model.GameType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameSession {
	
	private GameType gameType;
	private List<PlayerConnection> playerConnections;
	private GameState gameState;
	private long lastUpdateTimeMS;
	private boolean active;
	private boolean matchmakingComplete;

}
