package de.instinct.eqfleetgameserver.service.impl.matchmaking;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;

import de.instinct.eqfleetgameserver.service.model.GameSession;
import de.instinct.eqfleetgameserver.service.model.PlayerConnection;
import de.instinct.eqfleetshared.gamelogic.EngineUtility;
import de.instinct.eqfleetshared.gamelogic.model.GameType;
import de.instinct.eqfleetshared.net.message.types.MatchmakingRequest;

public class Matchmaker {
	
	private List<GameSession> unmatchedSessions;
	
	public Matchmaker() {
		unmatchedSessions = new ArrayList<>();
	}

	public GameSession process(Connection connection, MatchmakingRequest matchmakingRequest) {
		GameType requestedGameType = map(matchmakingRequest);
		GameSession unmatchedSession = getUnmatchedSession(requestedGameType);
		if (unmatchedSession == null) {
			unmatchedSession = GameSession.builder()
			.gameType(requestedGameType)
			.playerConnections(new ArrayList<>())
			.build();
			unmatchedSessions.add(unmatchedSession);
		}
		
		PlayerConnection newPlayerConnection = new PlayerConnection();
		newPlayerConnection.connection = connection;
		newPlayerConnection.playerUUID = matchmakingRequest.playerUUID;
		unmatchedSession.getPlayerConnections().add(newPlayerConnection);
		
		if (unmatchedSession.getPlayerConnections().size() == EngineUtility.totalPlayerCount(requestedGameType)) {
			unmatchedSession.setMatchmakingComplete(true);
			unmatchedSessions.remove(unmatchedSession);
		}
		return unmatchedSession;
	}
	
	private GameSession getUnmatchedSession(GameType requestedGameType) {
		for (GameSession unmatchedSession : unmatchedSessions) {
			if (unmatchedSession.getGameType().equals(requestedGameType)) {
				return unmatchedSession;
			}
		}
		return null;
	}

	private GameType map(MatchmakingRequest matchmakingRequest) {
		GameType gameType = new GameType();
		gameType.factionMode = matchmakingRequest.factionMode;
		gameType.gameMode = matchmakingRequest.gameMode;
		gameType.versusMode = matchmakingRequest.versusMode;
		return gameType;
	}

}
