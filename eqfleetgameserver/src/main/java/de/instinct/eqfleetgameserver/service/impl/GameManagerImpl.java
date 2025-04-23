package de.instinct.eqfleetgameserver.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;

import de.instinct.eqfleetgameserver.config.GameserverConfig;
import de.instinct.eqfleetgameserver.service.GameManager;
import de.instinct.eqfleetshared.net.KryoRegistrator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameManagerImpl implements GameManager {
	
	private Server server;
	private ServerConnectionListener connectionListener;
	
	private final GameserverConfig gameserverConfig;
	
	@Override
	public void start() {
		connectionListener = new ServerConnectionListener();
		server = new Server();
		Kryo kryo = server.getKryo();
        KryoRegistrator.registerAll(kryo);
		server.addListener(connectionListener);
		server.start();
		try {
			server.bind(gameserverConfig.getTcpPort(), gameserverConfig.getUdpPort());
			System.out.println("Server successfully started for ports - TCP: " + gameserverConfig.getTcpPort() + ", UDP: " + gameserverConfig.getUdpPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		server.stop();
		connectionListener.dispose();
	}

}
