package de.instinct.eqfleet.game.backend.driver.local.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.api.matchmaking.model.VersusMode;
import de.instinct.api.meta.dto.LoadoutData;
import de.instinct.api.meta.dto.ShipData;
import de.instinct.engine.EngineUtility;
import de.instinct.engine.ai.AiDifficulty;
import de.instinct.engine.ai.AiEngine;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.ship.Ship;
import de.instinct.engine.model.ship.ShipType;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;

public class CustomLoader {

	private AiEngine aiEngine;
	
	public CustomLoader() {
		aiEngine = new AiEngine();
	}
	
	public GameState generateGameState(LoadoutData loadout) {
		GameState initialGameState = new GameState();
		initialGameState.gameUUID = UUID.randomUUID().toString();
		initialGameState.planets = generateMap(GameType.builder()
				.versusMode(VersusMode.AI)
				.factionMode(FactionMode.ONE_VS_ONE)
				.gameMode(GameMode.CONQUEST)
				.build());
		initialGameState.players = loadPlayers(loadout);
		initialGameState.gameTimeMS = 0;
		initialGameState.maxGameTimeMS = 180_000;
		initialGameState.activeEvents = new PriorityQueue<>();
		initialGameState.winner = 0;
		initialGameState.atpToWin = 50;
		initialGameState.teamATPs = new HashMap<>();
		initialGameState.teamATPs.put(0, 0D);
		initialGameState.teamATPs.put(1, 0D);
		initialGameState.teamATPs.put(2, 0D);
		return initialGameState;
	}

	public List<Player> loadPlayers(LoadoutData loadout) {
		List<Player> players = new ArrayList<>();
		
		Player neutralPlayer = new Player();
		neutralPlayer.playerId = 0;
		neutralPlayer.teamId = 0;
		neutralPlayer.maxPlanetCapacity = 50;
		players.add(neutralPlayer);
		
		Player userPlayer = new Player();
		userPlayer.playerId = 1;
		userPlayer.name = ProfileModel.profile.getUsername() == null ? "???" : ProfileModel.profile.getUsername();
		userPlayer.teamId = 1;
		userPlayer.resourceGenerationSpeed = loadout.getResourceGenerationSpeed();
		userPlayer.commandPointsGenerationSpeed = loadout.getCommandPointsGenerationSpeed();
		userPlayer.startCommandPoints = loadout.getStartCommandPoints();
		userPlayer.currentCommandPoints = userPlayer.startCommandPoints;
		userPlayer.maxCommandPoints = loadout.getMaxCommandPoints();
		userPlayer.maxPlanetCapacity = loadout.getMaxPlanetCapacity();
		userPlayer.ships = getShips(loadout);
		players.add(userPlayer);
		
		AiPlayer aiPlayer = aiEngine.initialize(AiDifficulty.RETARDED);
		aiPlayer.playerId = 2;
		aiPlayer.teamId = 2;
		aiPlayer.connected = true;
		aiPlayer.loaded = true;
		players.add(aiPlayer);
		
		return players;
	}

	private List<Ship> getShips(LoadoutData loadout) {
		List<Ship> ships = new ArrayList<>();
		for (ShipData userShip : loadout.getShips()) {
			Ship ship = new Ship();
			ship.type = ShipType.valueOf(userShip.getType().toString());
			ship.cost = userShip.getCost();
			ship.model = userShip.getModel();
			ship.movementSpeed = userShip.getMovementSpeed();
			ship.power = userShip.getPower();
			ships.add(ship);
		}
		return ships;
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
    	startPlanetPlayerOne.yPos = -(EngineUtility.MAP_BOUNDS.y / 2) + 300;
    	planets.add(startPlanetPlayerOne);
    	
    	Planet startPlanetPlayerTwo = new Planet();
    	startPlanetPlayerTwo.id = 1;
    	startPlanetPlayerTwo.ownerId = gameType.factionMode.teamPlayerCount + 1;
    	startPlanetPlayerTwo.value = 10;
    	startPlanetPlayerTwo.xPos = 0;
    	startPlanetPlayerTwo.yPos = (EngineUtility.MAP_BOUNDS.y / 2) - 300;
    	planets.add(startPlanetPlayerTwo);
    	
    	if (gameType.factionMode.teamPlayerCount >= 2) {
    		Planet startPlanetPlayerThree = new Planet();
    		startPlanetPlayerThree.id = 2;
    		startPlanetPlayerThree.ownerId = 2;
    		startPlanetPlayerThree.value = 10;
    		startPlanetPlayerThree.xPos = -200;
    		startPlanetPlayerThree.yPos = -(EngineUtility.MAP_BOUNDS.y / 2) + 300;
        	planets.add(startPlanetPlayerThree);
        	
        	Planet startPlanetPlayerFour = new Planet();
        	startPlanetPlayerFour.id = 3;
        	startPlanetPlayerFour.ownerId = gameType.factionMode.teamPlayerCount + 2;
        	startPlanetPlayerFour.value = 10;
        	startPlanetPlayerFour.xPos = 200;
        	startPlanetPlayerFour.yPos = (EngineUtility.MAP_BOUNDS.y / 2) - 300;
        	planets.add(startPlanetPlayerFour);
		}
    	
    	if (gameType.factionMode.teamPlayerCount >= 3) {
    		Planet startPlanetPlayerFive = new Planet();
    		startPlanetPlayerFive.id = 4;
    		startPlanetPlayerFive.ownerId = 3;
    		startPlanetPlayerFive.value = 10;
    		startPlanetPlayerFive.xPos = 200;
    		startPlanetPlayerFive.yPos = -(EngineUtility.MAP_BOUNDS.y / 2) + 300;
        	planets.add(startPlanetPlayerFive);
        	
        	Planet startPlanetPlayerSix = new Planet();
        	startPlanetPlayerSix.id = 5;
        	startPlanetPlayerSix.ownerId = gameType.factionMode.teamPlayerCount + 3;
        	startPlanetPlayerSix.value = 10;
        	startPlanetPlayerSix.xPos = -200;
        	startPlanetPlayerSix.yPos = (EngineUtility.MAP_BOUNDS.y / 2) - 300;
        	planets.add(startPlanetPlayerSix);
		}
	}

	private void generateNeutralPlanets(List<Planet> planets) {
		Planet neutralPlanet2 = new Planet();
    	neutralPlanet2.id = 6;
    	neutralPlanet2.ownerId = 0;
    	neutralPlanet2.value = 30;
    	neutralPlanet2.xPos = -250;
    	neutralPlanet2.yPos = 0;
    	planets.add(neutralPlanet2);
    	
    	Planet neutralPlanet3 = new Planet();
    	neutralPlanet3.id = 7;
    	neutralPlanet3.ownerId = 0;
    	neutralPlanet3.value = 30;
    	neutralPlanet3.xPos = 250;
    	neutralPlanet3.yPos = 0;
    	planets.add(neutralPlanet3);
    	
    	Planet neutralPlanet4 = new Planet();
    	neutralPlanet4.id = 8;
    	neutralPlanet4.ownerId = 0;
    	neutralPlanet4.value = 20;
    	neutralPlanet4.xPos = 150;
    	neutralPlanet4.yPos = -250;
    	planets.add(neutralPlanet4);
    	
    	Planet neutralPlanet5 = new Planet();
    	neutralPlanet5.id = 9;
    	neutralPlanet5.ownerId = 0;
    	neutralPlanet5.value = 20;
    	neutralPlanet5.xPos = -150;
    	neutralPlanet5.yPos = 250;
    	planets.add(neutralPlanet5);
    	
    	Planet neutralPlanet6 = new Planet();
    	neutralPlanet6.id = 10;
    	neutralPlanet6.ownerId = 0;
    	neutralPlanet6.value = 10;
    	neutralPlanet6.xPos = 150;
    	neutralPlanet6.yPos = 400;
    	planets.add(neutralPlanet6);
    	
    	Planet neutralPlanet7 = new Planet();
    	neutralPlanet7.id = 11;
    	neutralPlanet7.ownerId = 0;
    	neutralPlanet7.value = 10;
    	neutralPlanet7.xPos = -150;
    	neutralPlanet7.yPos = -400;
    	planets.add(neutralPlanet7);
	}

	private void generateAncientPlanet(List<Planet> planets) {
		Planet ancientPlanet = new Planet();
    	ancientPlanet.id = 12;
    	ancientPlanet.ownerId = 0;
    	ancientPlanet.value = 0;
    	ancientPlanet.xPos = 0;
    	ancientPlanet.yPos = 0;
    	ancientPlanet.ancient = true;
    	planets.add(ancientPlanet);
	}

}
