package de.instinct.eqfleetgameserver.service.impl;

import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.instinct.eqfleetgameserver.service.impl.matchmaking.Matchmaker;
import de.instinct.eqfleetgameserver.service.model.GameSession;
import de.instinct.eqfleetgameserver.service.model.PlayerConnection;
import de.instinct.eqfleetshared.gamelogic.EngineUtility;
import de.instinct.eqfleetshared.net.message.types.FleetMovementMessage;
import de.instinct.eqfleetshared.net.message.types.MatchmakingRequest;
import de.instinct.eqfleetshared.net.message.types.MatchmakingUpdateResponse;

public class ServerConnectionListener extends Listener {
	
	private ArrayList<Connection> connections;
	private Matchmaker matchmaker;
	private SessionManager sessionManager;
	
	public ServerConnectionListener() {
		connections = new ArrayList<>();
		matchmaker = new Matchmaker();
		sessionManager = new SessionManager();
	}
	
	public void connected(Connection c) {
        connections.add(c);
    }

    public void received(Connection c, Object o) {
    	if (o instanceof MatchmakingRequest) {
    		GameSession newSession = matchmaker.process(c, (MatchmakingRequest) o);
    		if (newSession.isMatchmakingComplete()) {
    			sessionManager.add(newSession);
	            for (PlayerConnection pc : newSession.getPlayerConnections()) connections.remove(pc.connection);
    		} else {
    			MatchmakingUpdateResponse matchmakingUpdate = new MatchmakingUpdateResponse();
    			matchmakingUpdate.foundPlayers = newSession.getPlayerConnections().size();
    			matchmakingUpdate.requiredPlayers = EngineUtility.totalPlayerCount(newSession.getGameType());
    			for (PlayerConnection pc : newSession.getPlayerConnections()) pc.connection.sendTCP(matchmakingUpdate);
    		}
    	}
        if (o instanceof FleetMovementMessage) {
            FleetMovementMessage fleetMovement = (FleetMovementMessage) o;
            sessionManager.process(fleetMovement);
        }
    }

    public void disconnected(Connection c) {
        connections.remove(c);
        
    }

	public void dispose() {
		
	}

}
