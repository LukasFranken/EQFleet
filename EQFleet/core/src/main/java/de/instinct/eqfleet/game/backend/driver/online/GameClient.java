package de.instinct.eqfleet.game.backend.driver.online;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import de.instinct.engine.net.KryoRegistrator;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class GameClient {
	
	private final int PORT_TCP = 9006;
    private final int PORT_UDP = 9011;
    private final String ADDRESS = "eqgame.dev";
    //private final String ADDRESS = "localhost";
    private GameConnectionListener connectionListener;
    
    private final int PING_UPDATE_CLOCK_MS = 1000;
    private ScheduledExecutorService scheduler;
	
	public Client client;
	public boolean active;
	
	public GameClient() {
		client = new Client(65536, 65536);
        Kryo kryo = client.getKryo();
        KryoRegistrator.registerAll(kryo);
        connectionListener = new GameConnectionListener();
	}
	
	public void start() {
		try {
    		client.start();
    		client.addListener(connectionListener);
            client.connect(5000, ADDRESS, PORT_TCP, PORT_UDP);
            
            scheduler = Executors.newSingleThreadScheduledExecutor();
    		scheduler.scheduleAtFixedRate(() -> {
    			try {
    				if (client.isConnected()) {
        				client.updateReturnTripTime();
        				Console.updateMetric("gameserver_ping_MS", client.getReturnTripTime());
        			}
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}, 0, PING_UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            Logger.log("GameClient", "Failed to connect to server + " + e.getMessage(), ConsoleColor.RED);
            e.printStackTrace();
        }
		active = true;
	}
	
	public void send(NetworkMessage message) {
		if (client.isConnected()) {
			client.sendTCP(message);
			Logger.log("GameClient", "Message sent: " + message, ConsoleColor.PINK);
		}
	}
	
	public void stop() {
    	client.stop();
    	active = false;
	}

	public void dispose() {
		if (scheduler != null) {
            scheduler.shutdownNow();
        }
		if (client.isConnected()) {
			client.stop();
		}
		try {
			client.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
