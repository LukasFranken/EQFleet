package de.instinct.eqfleet.game.backend;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.net.message.NetworkMessage;
import de.instinct.eqfleetshared.net.message.types.PlayerAssigned;

public class GameConnectionListener extends Listener {

	private Client client;
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> messageSenderTask;

	public GameConnectionListener(Client client) {
		this.client = client;
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void connected(Connection connection) {
		messageSenderTask = scheduler.scheduleAtFixedRate(() -> {
			NetworkMessage newMessage = Game.outputMessageQueue.next();
			if (newMessage != null) {
				client.sendTCP(newMessage);
			}
		}, 50, 50, TimeUnit.MILLISECONDS);
	}

	@Override
	public void received(Connection c, Object o) {
		System.out.println("object received: " + o.getClass());
		if (o instanceof PlayerAssigned) {
			Game.assignPlayer((PlayerAssigned) o);
		} else if (o instanceof GameState) {
			Game.update((GameState) o);
		}
	}

	@Override
	public void disconnected(Connection connection) {
		if (messageSenderTask != null && !messageSenderTask.isCancelled()) {
			messageSenderTask.cancel(true);
		}
	}

	public void dispose() {
		scheduler.shutdownNow();
	}
	
}