package de.instinct.eqfleet.mining.driver.online;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import de.instinct.engine.core.net.NetworkMessage;
import de.instinct.engine.mining.net.MiningKryoRegistrator;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class MiningClient {
	
	private final int PORT_TCP = 8998;
    private final int PORT_UDP = 9012;
    private final String ADDRESS = "eqgame.dev";
    
    private final int PING_UPDATE_CLOCK_MS = 1000;
    private ScheduledExecutorService scheduler;
	public Client client;
	public boolean active;
	
	public MiningClient() {
		client = new Client(8096, 8096);
        Kryo kryo = client.getKryo();
        new MiningKryoRegistrator().registerClasses(kryo);
	}
	
	public void start() {
		try {
    		client.start();
    		client.addListener(new MiningConnectionListener());
            client.connect(5000, ADDRESS, PORT_TCP, PORT_UDP);
            
            scheduler = Executors.newSingleThreadScheduledExecutor();
    		scheduler.scheduleAtFixedRate(() -> {
    			try {
    				if (client.isConnected()) {
        				client.updateReturnTripTime();
        				Console.updateMetric("miningserver_ping_MS", client.getReturnTripTime());
        			}
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}, 0, PING_UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            Logger.log("MiningClient", "Failed to connect to server + " + e.getMessage(), ConsoleColor.RED);
            e.printStackTrace();
        }
		active = true;
	}
	
	public void stop() {
    	client.stop();
    	active = false;
	}
	
	public void sendMessage(NetworkMessage message) {
		if (client.isConnected()) {
			client.sendTCP(message);
			Logger.log("GameClient", "Message sent: " + message, ConsoleColor.PINK);
		}
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
