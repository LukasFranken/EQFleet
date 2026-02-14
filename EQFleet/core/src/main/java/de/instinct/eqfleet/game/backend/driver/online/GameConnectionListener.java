package de.instinct.eqfleet.game.backend.driver.online;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.metrics.types.NumberMetric;

public class GameConnectionListener extends Listener {

	@Override
	public void connected(Connection connection) {
		Console.registerMetric(NumberMetric.builder()
				.tag("gameserver_ping_MS")
				.build());
	}

	@Override
	public void received(Connection c, Object o) {
		if (o instanceof FrameworkMessage) {
			
		} else {
			Logger.log("Connection Listener", "object received: " + o, ConsoleColor.CYAN);
			GameModel.inputMessageQueue.add(o);
		}
	}

	@Override
	public void disconnected(Connection connection) {
		Console.remove("gameserver_ping_MS");
	}
	
}