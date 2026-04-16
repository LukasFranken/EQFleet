package de.instinct.eqfleet.game.backend.driver.local.custom;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.api.matchmaking.model.VersusMode;
import de.instinct.api.meta.dto.LoadoutData;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.ai.data.AiPlayer;
import de.instinct.engine_api.ai.service.AIPlayerLoader;
import de.instinct.engine_api.ai.service.NeutralPlayerLoader;
import de.instinct.engine_api.core.model.GameMap;
import de.instinct.engine_api.core.model.PlanetInitialization;
import de.instinct.engine_api.core.service.EngineDataInterface;
import de.instinct.engine_api.fleet.model.FleetGameStateInitialization;

public class CustomLoader {

	private AIPlayerLoader aiPlayerLoader;
	private NeutralPlayerLoader neutralPlayerLoader;

	public CustomLoader() {
		aiPlayerLoader = new AIPlayerLoader();
		neutralPlayerLoader = new NeutralPlayerLoader();
	}
	
	public FleetGameStateInitialization generateInitialGameState(LoadoutData loadout, int threatLevel) {
		FleetGameStateInitialization initialization = new FleetGameStateInitialization();
		initialization.setGameUUID("custom");
		initialization.setPlayers(loadPlayers(loadout, threatLevel));
		initialization.setMap(generateMap(GameType.builder()
				.factionMode(FactionMode.ONE_VS_ONE)
				.gameMode(GameMode.CONQUEST)
				.versusMode(VersusMode.AI)
				.build()));
		initialization.setGameTimeLimitMS(180_000);
		initialization.setAtpToWin(50);
		initialization.setPauseTimeLimitMS(300_000);
		initialization.setPauseCountLimit(100);
		return initialization;
	}

	public List<Player> loadPlayers(LoadoutData loadout, int threatLevel) {
		List<Player> players = new ArrayList<>();
		
		Player neutralPlayer = neutralPlayerLoader.createNeutralPlayer(threatLevel);
		neutralPlayer.id = 0;
		neutralPlayer.teamId = 0;
		neutralPlayer.name = "Neutral Player";
		players.add(neutralPlayer);
		
		Player userPlayer = EngineDataInterface.getPlayer(loadout);
		userPlayer.id = 1;
		userPlayer.teamId = 1;
		players.add(userPlayer);
		
		AiPlayer aiPlayer = aiPlayerLoader.initialize(threatLevel);
		aiPlayer.id = 4;
		aiPlayer.teamId = 2;
		players.add(aiPlayer);
		
		return players;
	}

	private GameMap generateMap(GameType gameType) {
		GameMap map = new GameMap();
		List<PlanetInitialization> planets = new ArrayList<>();
		generateAncientPlanet(planets);
		generateNeutralPlanets(planets);
		generatePlayerPlanets(planets, gameType);
		map.setPlanets(planets);
		map.setZoomFactor(1f);
		map.setAncientPlanetResourceDegradationFactor(0.1f);
		return map;
	}

	private void generatePlayerPlanets(List<PlanetInitialization> planets, GameType gameType) {
		PlanetInitialization startPlanetPlayerOne = new PlanetInitialization();
    	startPlanetPlayerOne.setOwnerId(1);
    	startPlanetPlayerOne.setPosition(new Vector2(0, -600));
    	planets.add(startPlanetPlayerOne);
    	
    	PlanetInitialization startPlanetPlayerTwo = new PlanetInitialization();
    	startPlanetPlayerTwo.setOwnerId(4);
    	startPlanetPlayerTwo.setPosition(new Vector2(0, 600));
    	planets.add(startPlanetPlayerTwo);
    	
    	if (gameType.getFactionMode().teamPlayerCount >= 2) {
    		PlanetInitialization startPlanetPlayerThree = new PlanetInitialization();
    		startPlanetPlayerThree.setOwnerId(2);
    		startPlanetPlayerThree.setPosition(new Vector2(-200, -600));
        	planets.add(startPlanetPlayerThree);
        	
        	PlanetInitialization startPlanetPlayerFour = new PlanetInitialization();
        	startPlanetPlayerFour.setOwnerId(5);
        	startPlanetPlayerFour.setPosition(new Vector2(200, 600));
        	planets.add(startPlanetPlayerFour);
		}
    	
    	if (gameType.getFactionMode().teamPlayerCount >= 3) {
    		PlanetInitialization startPlanetPlayerFive = new PlanetInitialization();
    		startPlanetPlayerFive.setOwnerId(3);
    		startPlanetPlayerFive.setPosition(new Vector2(200, -600));
        	planets.add(startPlanetPlayerFive);
        	
        	PlanetInitialization startPlanetPlayerSix = new PlanetInitialization();
        	startPlanetPlayerSix.setOwnerId(6);
        	startPlanetPlayerSix.setPosition(new Vector2(-200, 600));
        	planets.add(startPlanetPlayerSix);
		}
	}

	private void generateNeutralPlanets(List<PlanetInitialization> planets) {
		PlanetInitialization neutralPlanet2 = new PlanetInitialization();
		neutralPlanet2.setOwnerId(0);
		neutralPlanet2.setPosition(new Vector2(0, -250));
    	planets.add(neutralPlanet2);
    	
    	PlanetInitialization neutralPlanet3 = new PlanetInitialization();
    	neutralPlanet3.setOwnerId(0);
    	neutralPlanet3.setPosition(new Vector2(0, 250));
    	planets.add(neutralPlanet3);
    	
    	PlanetInitialization neutralPlanet4 = new PlanetInitialization();
    	neutralPlanet4.setOwnerId(0);
    	neutralPlanet4.setPosition(new Vector2(250, 0));
    	planets.add(neutralPlanet4);
    	
    	PlanetInitialization neutralPlanet5 = new PlanetInitialization();
    	neutralPlanet5.setOwnerId(0);
    	neutralPlanet5.setPosition(new Vector2(-250, 0));
    	planets.add(neutralPlanet5);
    	
    	PlanetInitialization neutralPlanet6 = new PlanetInitialization();
    	neutralPlanet6.setOwnerId(0);
    	neutralPlanet6.setPosition(new Vector2(250, 400));
    	planets.add(neutralPlanet6);
    	
    	PlanetInitialization neutralPlanet7 = new PlanetInitialization();
    	neutralPlanet7.setOwnerId(0);
    	neutralPlanet7.setPosition(new Vector2(-250, -400));
    	planets.add(neutralPlanet7);
	}

	private void generateAncientPlanet(List<PlanetInitialization> planets) {
		PlanetInitialization ancientPlanet = new PlanetInitialization();
    	ancientPlanet.setOwnerId(0);
    	ancientPlanet.setPosition(new Vector2(0, 0));;
    	ancientPlanet.setAncient(true);
    	planets.add(ancientPlanet);
	}

}
