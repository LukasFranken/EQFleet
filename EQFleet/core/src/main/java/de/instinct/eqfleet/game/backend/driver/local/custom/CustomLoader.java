package de.instinct.eqfleet.game.backend.driver.local.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.math.Vector2;

import de.instinct.api.core.API;
import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.api.matchmaking.model.VersusMode;
import de.instinct.api.meta.dto.LoadoutData;
import de.instinct.engine.ai.AiEngine;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.PlanetData;
import de.instinct.engine.model.ship.components.HullData;
import de.instinct.engine.model.ship.components.ShieldData;
import de.instinct.engine.model.ship.components.WeaponData;
import de.instinct.engine.model.ship.components.types.ShieldType;
import de.instinct.engine.model.ship.components.types.WeaponType;
import de.instinct.engine.model.turret.PlatformData;
import de.instinct.engine.model.turret.PlatformType;
import de.instinct.engine.model.turret.TurretData;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine_api.core.model.GameMap;
import de.instinct.engine_api.core.model.GameStateInitialization;
import de.instinct.engine_api.core.model.PlanetInitialization;
import de.instinct.engine_api.core.service.EngineDataInterface;

public class CustomLoader {

	private AiEngine aiEngine;
	
	public CustomLoader() {
		aiEngine = new AiEngine();
	}
	
	public GameStateInitialization generateInitialGameState(LoadoutData loadout, int threatLevel) {
		GameStateInitialization initialization = new GameStateInitialization();
		initialization.setGameUUID(UUID.randomUUID().toString());;
		initialization.setPlayers(loadPlayers(loadout, threatLevel));
		initialization.setMap(generateMap(GameType.builder()
				.factionMode(FactionMode.ONE_VS_ONE)
				.gameMode(GameMode.CONQUEST)
				.versusMode(VersusMode.AI)
				.build()));
		initialization.setAncientPlanetResourceDegradationFactor(0.5f);;
		initialization.setGameTimeLimitMS(180_000);
		initialization.setAtpToWin(50);
		initialization.setPauseTimeLimitMS(300_000);
		initialization.setPauseCountLimit(100);
		return initialization;
	}

	public List<Player> loadPlayers(LoadoutData loadout, int threatLevel) {
		List<Player> players = new ArrayList<>();
		
		Player neutralPlayer = createNeutralPlayer(threatLevel);
		neutralPlayer.id = 0;
		neutralPlayer.teamId = 0;
		neutralPlayer.name = "Neutral Player";
		players.add(neutralPlayer);
		
		Player userPlayer = getPlayer(loadout);
		userPlayer.id = 1;
		userPlayer.teamId = 1;
		players.add(userPlayer);
		
		AiPlayer aiPlayer = aiEngine.initialize(threatLevel);
		aiPlayer.id = 4;
		aiPlayer.teamId = 2;
		players.add(aiPlayer);
		
		return players;
	}
	
	private Player createNeutralPlayer(int threatLevel) {
		Player neutralPlayer = new Player();
		neutralPlayer.commandPointsGenerationSpeed = 0;
		neutralPlayer.startCommandPoints = 0;
		neutralPlayer.maxCommandPoints = 0;
		PlanetData neutralPlanetData = new PlanetData();
		neutralPlanetData.resourceGenerationSpeed = 0;
		neutralPlanetData.maxResourceCapacity = 0;
		neutralPlayer.planetData = neutralPlanetData;
		
		neutralPlayer.turrets = new ArrayList<>();
		TurretData neutralTurret = new TurretData();
		neutralTurret.model = "projectile";
		neutralTurret.cpCost = 0;
		neutralTurret.resourceCost = 0;
		
		PlatformData neutralTurretPlatform = new PlatformData();
		neutralTurretPlatform.type = PlatformType.SERVO;
		neutralTurretPlatform.rotationSpeed = 1f;
		neutralTurret.platform = neutralTurretPlatform;
		
		HullData neutralTurretHull = new HullData();
		neutralTurretHull.strength = 50;
		neutralTurret.hull = neutralTurretHull;
		
		neutralTurret.weapons = new ArrayList<>();
		WeaponData neutralTurretWeapon = new WeaponData();
		neutralTurretWeapon.type = WeaponType.MISSILE;
		neutralTurretWeapon.damage = 2 + (2 * ((float)threatLevel/100f));;
		neutralTurretWeapon.range = 100f;
		neutralTurretWeapon.cooldown = 1000;
		neutralTurretWeapon.speed = 120f;
		neutralTurret.weapons.add(neutralTurretWeapon);
		
		neutralTurret.shields = new ArrayList<>();
		ShieldData neutralTurretShield1 = new ShieldData();
		neutralTurretShield1.type = ShieldType.NULLPOINT;
		neutralTurretShield1.strength = 3f;
		neutralTurretShield1.generation = 0.2f;
		neutralTurret.shields.add(neutralTurretShield1);
		ShieldData neutralTurretShield2 = new ShieldData();
		neutralTurretShield2.type = ShieldType.PLASMA;
		neutralTurretShield2.strength = 10f;
		neutralTurretShield2.generation = 2f;
		neutralTurret.shields.add(neutralTurretShield2);
		neutralPlayer.turrets.add(neutralTurret);
		
		neutralPlayer.ships = new ArrayList<>();
		return neutralPlayer;
	}
	
	private Player getPlayer(LoadoutData loadout) {
		Player newPlayer = new Player();
		newPlayer.commandPointsGenerationSpeed = loadout.getCommander().getCommandPointsGenerationSpeed();
		newPlayer.startCommandPoints = loadout.getCommander().getStartCommandPoints();
		newPlayer.maxCommandPoints = loadout.getCommander().getMaxCommandPoints();
		newPlayer.planetData = EngineDataInterface.getPlanetData(loadout.getPlayerInfrastructure(), API.construction().construction());
		newPlayer.ships = EngineDataInterface.getShips(loadout.getShips(), API.shipyard().shipyard());
		newPlayer.turrets = EngineDataInterface.getPlayerTurretData(loadout.getPlayerInfrastructure().getPlayerTurrets().get(0), API.construction().construction());
		return newPlayer;
	}

	private GameMap generateMap(GameType gameType) {
		GameMap map = new GameMap();
		List<PlanetInitialization> planets = new ArrayList<>();
		generateAncientPlanet(planets);
		generateNeutralPlanets(planets);
		generatePlayerPlanets(planets, gameType);
		map.setPlanets(planets);
		map.setZoomFactor(1f);
		return map;
	}

	private void generatePlayerPlanets(List<PlanetInitialization> planets, GameType gameType) {
		PlanetInitialization startPlanetPlayerOne = new PlanetInitialization();
    	startPlanetPlayerOne.setOwnerId(1);
    	startPlanetPlayerOne.setPosition(new Vector2(0, -(EngineUtility.MAP_BOUNDS.y / 2) + 300));
    	planets.add(startPlanetPlayerOne);
    	
    	PlanetInitialization startPlanetPlayerTwo = new PlanetInitialization();
    	startPlanetPlayerTwo.setOwnerId(4);
    	startPlanetPlayerTwo.setPosition(new Vector2(0, (EngineUtility.MAP_BOUNDS.y / 2) - 300));
    	planets.add(startPlanetPlayerTwo);
    	
    	if (gameType.getFactionMode().teamPlayerCount >= 2) {
    		PlanetInitialization startPlanetPlayerThree = new PlanetInitialization();
    		startPlanetPlayerThree.setOwnerId(2);
    		startPlanetPlayerThree.setPosition(new Vector2(-200, (EngineUtility.MAP_BOUNDS.y / 2) + 300));
        	planets.add(startPlanetPlayerThree);
        	
        	PlanetInitialization startPlanetPlayerFour = new PlanetInitialization();
        	startPlanetPlayerFour.setOwnerId(5);
        	startPlanetPlayerFour.setPosition(new Vector2(200, (EngineUtility.MAP_BOUNDS.y / 2) - 300));
        	planets.add(startPlanetPlayerFour);
		}
    	
    	if (gameType.getFactionMode().teamPlayerCount >= 3) {
    		PlanetInitialization startPlanetPlayerFive = new PlanetInitialization();
    		startPlanetPlayerFive.setOwnerId(3);
    		startPlanetPlayerFive.setPosition(new Vector2(200, -(EngineUtility.MAP_BOUNDS.y / 2) + 300));
        	planets.add(startPlanetPlayerFive);
        	
        	PlanetInitialization startPlanetPlayerSix = new PlanetInitialization();
        	startPlanetPlayerSix.setOwnerId(6);
        	startPlanetPlayerSix.setPosition(new Vector2(-200, (EngineUtility.MAP_BOUNDS.y / 2) - 300));
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
