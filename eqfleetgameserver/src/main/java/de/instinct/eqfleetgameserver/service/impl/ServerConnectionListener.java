package de.instinct.eqfleetgameserver.service.impl;

import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.instinct.eqfleetshared.net.message.types.FleetMovementMessage;
import de.instinct.eqfleetshared.net.message.types.JoinMessage;

public class ServerConnectionListener extends Listener {
	
	private ArrayList<Connection> connections;
	
	public ServerConnectionListener() {
		connections = new ArrayList<>();
	}
	
	public void connected(Connection c) {
        connections.add(c);
    }

    public void received(Connection c, Object o) {
    	if (o instanceof JoinMessage) {
            
        }
        if (o instanceof FleetMovementMessage) {
            FleetMovementMessage fleetMovement = (FleetMovementMessage) o;
            SessionManager.process(fleetMovement);
        }
    }

    public void disconnected(Connection c) {
        connections.remove(c);
        
    }

	public void dispose() {
		
	}

}
