package de.instinct.eqfleet.game.backend.driver.online;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import de.instinct.engine.net.KryoRegistrator;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class GameClient {
	
	private final int PORT_TCP = 9006;
    private final int PORT_UDP = 9011;
    private final String ADDRESS = "eqgame.dev";
    //private final String ADDRESS = "localhost";
    private GameConnectionListener connectionListener;
	
	public Client client;
	public boolean active;
	
	public GameClient() {
		client = new Client(8192, 8192);
        Kryo kryo = client.getKryo();
        KryoRegistrator.registerAll(kryo);
        connectionListener = new GameConnectionListener();
	}
	
	public void start() {
		try {
    		client.start();
    		client.addListener(connectionListener);
            client.connect(5000, ADDRESS, PORT_TCP, PORT_UDP);
        } catch (IOException e) {
            Gdx.app.error("GameClient", "Failed to connect to server", e);
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
