package de.instinct.eqfleetgameserver.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import de.instinct.eqfleetshared.gamelogic.EngineUtility;
import de.instinct.eqfleetshared.gamelogic.ai.AiDifficulty;
import de.instinct.eqfleetshared.gamelogic.ai.AiEngine;
import de.instinct.eqfleetshared.gamelogic.model.AiPlayer;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.gamelogic.model.GameType;
import de.instinct.eqfleetshared.gamelogic.model.Planet;
import de.instinct.eqfleetshared.gamelogic.model.Player;
import de.instinct.eqfleetshared.net.enums.VersusMode;

public class GameDataLoader {
	
	private AiEngine aiEngine;
	
	public GameDataLoader() {
		aiEngine = new AiEngine();
	}
	
	public GameState generateGameState(GameType gameType) {
		GameState initialGameState = new GameState();
		initialGameState.gameUUID = UUID.randomUUID().toString();
		initialGameState.planets = generateMap(gameType);
		initialGameState.players = loadPlayers(gameType);
		initialGameState.gameTimeMS = 0;
		initialGameState.maxGameTimeMS = 180_000;
		initialGameState.activeEvents = new PriorityQueue<>();
		initialGameState.winner = 0;
		initialGameState.atpToWin = 50;
		return initialGameState;
	}

	private List<Player> loadPlayers(GameType gameType) {
		List<Player> players = new ArrayList<>();
		Player player1 = new Player();
		player1.factionId = 1;
		player1.name = "Player 1";
		player1.fleetMovementSpeed = 50f;
		player1.resourceGenerationSpeed = 1f;
		player1.maxCommandPoints = 10;
		player1.startCommandPoints = 3;
		player1.commandPointsGenerationSpeed = 0.1;
		player1.currentCommandPoints = player1.startCommandPoints;
		players.add(player1);
		
		if (gameType.versusMode == VersusMode.AI) {
			AiPlayer aiPlayer = aiEngine.initialize(AiDifficulty.RETARDED);
			aiPlayer.factionId = 2;
			aiPlayer.currentCommandPoints = aiPlayer.startCommandPoints;
			players.add(aiPlayer);
		} else {
			Player player2 = new Player();
			player2.factionId = 2;
			player2.name = "Player 2";
			player2.fleetMovementSpeed = 50f;
			player2.resourceGenerationSpeed = 1f;
			player2.maxCommandPoints = 10;
			player2.startCommandPoints = 3;
			player2.commandPointsGenerationSpeed = 0.1;
			player2.currentCommandPoints = player2.startCommandPoints;
			players.add(player2);
		}
		
		return players;
	}

	private List<Planet> generateMap(GameType gameType) {
		List<Planet> planets = new ArrayList<>();
		generateAncientPlanet(planets);
		generateNeutralPlanets(planets);
		generatePlayerPlanets(planets, gameType);
		return planets;
	}

	private void generatePlayerPlanets(List<Planet> planets, GameType gameType) {
		Planet startPlanetPlayerOne = new Planet();
    	startPlanetPlayerOne.id = 0;
    	startPlanetPlayerOne.ownerId = 1;
    	startPlanetPlayerOne.value = 10;
    	startPlanetPlayerOne.xPos = 0;
    	startPlanetPlayerOne.yPos = -(EngineUtility.MAP_BOUNDS.y / 2) + EngineUtility.PLANET_RADIUS + 350;
    	planets.add(startPlanetPlayerOne);
    	
    	Planet startPlanetPlayerTwo = new Planet();
    	startPlanetPlayerTwo.id = 1;
    	startPlanetPlayerTwo.ownerId = 2;
    	startPlanetPlayerTwo.value = 10;
    	startPlanetPlayerTwo.xPos = 0;
    	startPlanetPlayerTwo.yPos = (EngineUtility.MAP_BOUNDS.y / 2) - EngineUtility.PLANET_RADIUS - 350;
    	planets.add(startPlanetPlayerTwo);
    	
    	if (gameType.factionMode.teamPlayerCount >= 2) {
    		Planet startPlanetPlayerThree = new Planet();
    		startPlanetPlayerThree.id = 0;
    		startPlanetPlayerThree.ownerId = 3;
    		startPlanetPlayerThree.value = 10;
    		startPlanetPlayerThree.xPos = -200;
    		startPlanetPlayerThree.yPos = -(EngineUtility.MAP_BOUNDS.y / 2) + EngineUtility.PLANET_RADIUS + 350;
        	planets.add(startPlanetPlayerThree);
        	
        	Planet startPlanetPlayerFour = new Planet();
        	startPlanetPlayerFour.id = 1;
        	startPlanetPlayerFour.ownerId = 4;
        	startPlanetPlayerFour.value = 10;
        	startPlanetPlayerFour.xPos = 200;
        	startPlanetPlayerFour.yPos = (EngineUtility.MAP_BOUNDS.y / 2) - EngineUtility.PLANET_RADIUS - 350;
        	planets.add(startPlanetPlayerFour);
		}
    	
    	if (gameType.factionMode.teamPlayerCount >= 3) {
    		Planet startPlanetPlayerFive = new Planet();
    		startPlanetPlayerFive.id = 0;
    		startPlanetPlayerFive.ownerId = 5;
    		startPlanetPlayerFive.value = 10;
    		startPlanetPlayerFive.xPos = 200;
    		startPlanetPlayerFive.yPos = -(EngineUtility.MAP_BOUNDS.y / 2) + EngineUtility.PLANET_RADIUS + 350;
        	planets.add(startPlanetPlayerFive);
        	
        	Planet startPlanetPlayerSix = new Planet();
        	startPlanetPlayerSix.id = 1;
        	startPlanetPlayerSix.ownerId = 6;
        	startPlanetPlayerSix.value = 10;
        	startPlanetPlayerSix.xPos = -200;
        	startPlanetPlayerSix.yPos = (EngineUtility.MAP_BOUNDS.y / 2) - EngineUtility.PLANET_RADIUS - 350;
        	planets.add(startPlanetPlayerSix);
		}
	}

	private void generateNeutralPlanets(List<Planet> planets) {
		Planet neutralPlanet2 = new Planet();
    	neutralPlanet2.id = 3;
    	neutralPlanet2.ownerId = 0;
    	neutralPlanet2.value = 30;
    	neutralPlanet2.xPos = -250;
    	neutralPlanet2.yPos = 0;
    	planets.add(neutralPlanet2);
    	
    	Planet neutralPlanet3 = new Planet();
    	neutralPlanet3.id = 4;
    	neutralPlanet3.ownerId = 0;
    	neutralPlanet3.value = 30;
    	neutralPlanet3.xPos = 250;
    	neutralPlanet3.yPos = 0;
    	planets.add(neutralPlanet3);
    	
    	Planet neutralPlanet4 = new Planet();
    	neutralPlanet4.id = 5;
    	neutralPlanet4.ownerId = 0;
    	neutralPlanet4.value = 20;
    	neutralPlanet4.xPos = 150;
    	neutralPlanet4.yPos = -250;
    	planets.add(neutralPlanet4);
    	
    	Planet neutralPlanet5 = new Planet();
    	neutralPlanet5.id = 6;
    	neutralPlanet5.ownerId = 0;
    	neutralPlanet5.value = 20;
    	neutralPlanet5.xPos = -150;
    	neutralPlanet5.yPos = 250;
    	planets.add(neutralPlanet5);
    	
    	Planet neutralPlanet6 = new Planet();
    	neutralPlanet6.id = 7;
    	neutralPlanet6.ownerId = 0;
    	neutralPlanet6.value = 10;
    	neutralPlanet6.xPos = 150;
    	neutralPlanet6.yPos = 500;
    	planets.add(neutralPlanet6);
    	
    	Planet neutralPlanet7 = new Planet();
    	neutralPlanet7.id = 8;
    	neutralPlanet7.ownerId = 0;
    	neutralPlanet7.value = 10;
    	neutralPlanet7.xPos = -150;
    	neutralPlanet7.yPos = -500;
    	planets.add(neutralPlanet7);
	}

	private void generateAncientPlanet(List<Planet> planets) {
		Planet ancientPlanet = new Planet();
    	ancientPlanet.id = 2;
    	ancientPlanet.ownerId = 0;
    	ancientPlanet.value = 0;
    	ancientPlanet.xPos = 0;
    	ancientPlanet.yPos = 0;
    	ancientPlanet.ancient = true;
    	planets.add(ancientPlanet);
	}
	
}
