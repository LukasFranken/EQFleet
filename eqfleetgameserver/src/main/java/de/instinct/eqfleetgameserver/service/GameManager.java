package de.instinct.eqfleetgameserver.service;

import de.instinct.api.game.dto.GameserverInitializationRequest;

public interface GameManager {
	
	void start();

	void stop();

	void createSession(GameserverInitializationRequest lobby);

}
