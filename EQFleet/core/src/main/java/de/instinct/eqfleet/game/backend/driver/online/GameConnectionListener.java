package de.instinct.eqfleet.game.backend.driver.online;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class GameConnectionListener extends Listener {

	@Override
	public void connected(Connection connection) {
		
	}

	@Override
	public void received(Connection c, Object o) {
		Logger.log("Connection Listener", "object received: " + o, ConsoleColor.CYAN);
		GameModel.inputMessageQueue.add(o);
	}

	@Override
	public void disconnected(Connection connection) {
		
	}
	
}