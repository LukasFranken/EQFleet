package de.instinct.devtools.enginerunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.FleetEngine;
import de.instinct.engine.ai.AiEngine;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.PlanetData;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.components.CoreData;
import de.instinct.engine.model.ship.components.EngineData;
import de.instinct.engine.model.ship.components.HullData;
import de.instinct.engine.model.ship.components.ShieldData;
import de.instinct.engine.model.ship.components.WeaponData;
import de.instinct.engine.model.ship.components.types.CoreType;
import de.instinct.engine.model.ship.components.types.EngineType;
import de.instinct.engine.model.ship.components.types.ShieldType;
import de.instinct.engine.model.ship.components.types.WeaponType;
import de.instinct.engine.model.turret.PlatformData;
import de.instinct.engine.model.turret.PlatformType;
import de.instinct.engine.model.turret.TurretData;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.GameStatistic;
import de.instinct.engine_api.core.model.GameMap;
import de.instinct.engine_api.core.model.GameStateInitialization;
import de.instinct.engine_api.core.model.PlanetInitialization;
import de.instinct.engine_api.core.service.GameStateInitializer;

public class TestEngineManager {
	
	public static GameState state;
	
	private static FleetEngine engine;
	private static AiEngine aiEngine;
	private static GameStateInitializer gameStateInitializer;
	
	public static void init() {
		aiEngine = new AiEngine();
		engine = new FleetEngine();
		engine.initialize();
		gameStateInitializer = new GameStateInitializer();
		state = gameStateInitializer.initialize(getGameStateInitialization());
	}
	
	public static void update(long deltaTime) {
		engine.update(state, deltaTime);
		for (Player player : state.players) {
			if (player instanceof AiPlayer) {
				List<GameOrder> aiOrders = aiEngine.act((AiPlayer)player, state);
				for (GameOrder order : aiOrders) {
					engine.queue(state, order);
				}
			}
		}
		if (state.winner != 0) {
			GameStatistic stats = StatCollector.grab(state.gameUUID);
			if (stats != null) {
				System.out.println(stats);
			}
		}
	}
	
	public static void queue(GameOrder order) {
		engine.queue(state, order);
	}
	
	private static GameStateInitialization getGameStateInitialization() {
		GameStateInitialization init = new GameStateInitialization();
		init.setGameUUID(UUID.randomUUID().toString());
		init.setPlayers(initializePlayers(3));
		init.setMap(initializeMap(3));
		init.setAncientPlanetResourceDegradationFactor(0.5f);;
		init.setGameTimeLimitMS(180_000);
		init.setAtpToWin(50);
		init.setPauseTimeLimitMS(30_000);
		init.setPauseCountLimit(3);
		return init;
	}

	private static List<Player> initializePlayers(int playerCount) {
		List<Player> players= new ArrayList<>();
		
		Player neutralPlayer = createNeutralPlayer();
		neutralPlayer.id = 0;
		neutralPlayer.teamId = 0;
		neutralPlayer.name = "Neutral Player";
		players.add(neutralPlayer);
		
		Player player = createPlayer();
		player.id = 1;
		player.teamId = 1;
		player.name = "Player 1";
		players.add(player);
		
		AiEngine aiEngine = new AiEngine();
		Player aiPlayer = aiEngine.initialize(100);
		aiPlayer.id = 4;
		aiPlayer.teamId = 2;
		players.add(aiPlayer);
		
		if (playerCount > 1) {
			Player player2 = createPlayer();
			player2.id = 2;
			player2.teamId = 1;
			player2.name = "Player 2";
			players.add(player2);
			
			Player aiPlayer2 = aiEngine.initialize(500);
			aiPlayer2.id = 5;
			aiPlayer2.teamId = 2;
			players.add(aiPlayer2);
		}
		
		if (playerCount > 2) {
			Player player3 = createPlayer();
			player3.id = 3;
			player3.teamId = 1;
			player3.name = "Player 3";
			players.add(player3);
			
			Player aiPlayer3 = aiEngine.initialize(10);
			aiPlayer3.id = 6;
			aiPlayer3.teamId = 2;
			players.add(aiPlayer3);
		}
		return players;
	}
	
	private static Player createNeutralPlayer() {
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
		neutralTurretWeapon.damage = 3;
		neutralTurretWeapon.range = 100f;
		neutralTurretWeapon.cooldown = 1000;
		neutralTurretWeapon.speed = 80f;
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

	private static Player createPlayer() {
		Player player = new Player();
		player.commandPointsGenerationSpeed = 0.1;
		player.startCommandPoints = 3;
		player.maxCommandPoints = 10;
		
		PlanetData playerPlanetData = new PlanetData();
		playerPlanetData.resourceGenerationSpeed = 1f;
		playerPlanetData.maxResourceCapacity = 20;
		player.planetData = playerPlanetData;
		
		player.turrets = new ArrayList<>();
		TurretData playerTurret = new TurretData();
		playerTurret.model = "projectile";
		playerTurret.cpCost = 3;
		playerTurret.resourceCost = 10;
		
		PlatformData playerTurretPlatform = new PlatformData();
		playerTurretPlatform.type = PlatformType.SERVO;
		playerTurretPlatform.rotationSpeed = 1f;
		playerTurret.platform = playerTurretPlatform;
		
		HullData playerTurretHull = new HullData();
		playerTurretHull.strength = 30;
		playerTurret.hull = playerTurretHull;
		
		playerTurret.weapons = new ArrayList<>();
		WeaponData playerTurretWeapon = new WeaponData();
		playerTurretWeapon.type = WeaponType.PROJECTILE;
		playerTurretWeapon.damage = 20;
		playerTurretWeapon.range = 200f;
		playerTurretWeapon.cooldown = 2000;
		playerTurretWeapon.speed = 160f;
		playerTurret.weapons.add(playerTurretWeapon);
		
		playerTurret.shields = new ArrayList<>();
		ShieldData playerTurretShield = new ShieldData();
		playerTurretShield.type = ShieldType.PLASMA;
		playerTurretShield.strength = 20f;
		playerTurretShield.generation = 1f;
		playerTurret.shields.add(playerTurretShield);
		player.turrets.add(playerTurret);
		
		player.ships = new ArrayList<>();
		ShipData playerShipHawk = new ShipData();
		playerShipHawk.model = "hawk";
		playerShipHawk.cpCost = 1;
		playerShipHawk.resourceCost = 3;
		
		CoreData playerShipHawkCore = new CoreData();
		playerShipHawkCore.type = CoreType.FIGHTER;
		playerShipHawk.core = playerShipHawkCore;
		
		EngineData playerShipHawkEngine = new EngineData();
		playerShipHawkEngine.type = EngineType.ION;
		playerShipHawkEngine.speed = 60;
		playerShipHawkEngine.acceleration = 1f;
		playerShipHawk.engine = playerShipHawkEngine;
		
		HullData playerShipHawkHull = new HullData();
		playerShipHawkHull.strength = 1000;
		playerShipHawk.hull = playerShipHawkHull;

		playerShipHawk.weapons = new ArrayList<>();
		WeaponData playerShipWeaponHawk2 = new WeaponData();
		playerShipWeaponHawk2.type = WeaponType.PROJECTILE;
		playerShipWeaponHawk2.damage = 22;
		playerShipWeaponHawk2.range = 160f;
		playerShipWeaponHawk2.cooldown = 3000;
		playerShipWeaponHawk2.speed = 80f;
		playerShipHawk.weapons.add(playerShipWeaponHawk2);
		
		WeaponData playerShipWeaponHawk = new WeaponData();
		playerShipWeaponHawk.type = WeaponType.BEAM;
		playerShipWeaponHawk.damage = 5;
		playerShipWeaponHawk.range = 120f;
		playerShipWeaponHawk.cooldown = 2000;
		playerShipWeaponHawk.speed = 80f;
		playerShipHawk.weapons.add(playerShipWeaponHawk);
		
		playerShipHawk.shields = new ArrayList<>();
		ShieldData playerShipShieldHawk = new ShieldData();
		playerShipShieldHawk.type = ShieldType.PLASMA;
		playerShipShieldHawk.strength = 6f;
		playerShipShieldHawk.generation = 0.1f;
		playerShipHawk.shields.add(playerShipShieldHawk);
		player.ships.add(playerShipHawk);
		
		/*ShipData playerShipTurtle = new ShipData();
		playerShipTurtle.model = "turtle";
		playerShipTurtle.type = ShipType.FIGHTER;
		playerShipTurtle.movementSpeed = 100;
		playerShipTurtle.cost = 3;
		playerShipTurtle.commandPointsCost = 1;
		WeaponData playerShipWeaponTurtle = new WeaponData();
		playerShipWeaponTurtle.type = WeaponType.BEAM;
		playerShipWeaponTurtle.damage = 5;
		playerShipWeaponTurtle.range = 100f;
		playerShipWeaponTurtle.cooldown = 2000;
		playerShipWeaponTurtle.speed = 80f;
		playerShipTurtle.weapon = playerShipWeaponTurtle;
		Defense playerShipDefenseTurtle = new Defense();
		playerShipDefenseTurtle.shield = 6;
		playerShipDefenseTurtle.armor = 15;
		playerShipDefenseTurtle.shieldRegenerationSpeed = 0.1f;
		playerShipTurtle.defense = playerShipDefenseTurtle;
		player.ships.add(playerShipTurtle);
		
		ShipData playerShipShark = new ShipData();
		playerShipShark.model = "shark";
		playerShipShark.type = ShipType.FIGHTER;
		playerShipShark.movementSpeed = 100;
		playerShipShark.cost = 3;
		playerShipShark.commandPointsCost = 1;
		WeaponData playerShipWeaponShark = new WeaponData();
		playerShipWeaponShark.type = WeaponType.MISSILE;
		playerShipWeaponShark.damage = 5;
		playerShipWeaponShark.aoeRadius = 1000f;
		playerShipWeaponShark.range = 100f;
		playerShipWeaponShark.cooldown = 2000;
		playerShipWeaponShark.speed = 80f;
		playerShipShark.weapon = playerShipWeaponShark;
		Defense playerShipDefenseShark = new Defense();
		playerShipDefenseShark.shield = 6;
		playerShipDefenseShark.armor = 15;
		playerShipDefenseShark.shieldRegenerationSpeed = 0.1f;
		playerShipShark.defense = playerShipDefenseShark;
		player.ships.add(playerShipShark);*/
		return player;
	}

	private static GameMap initializeMap(int playerCount) {
		GameMap map = new GameMap();
		List<PlanetInitialization> planets = new ArrayList<>();
		/*PlanetInitialization neutralPlanet1 = new PlanetInitialization();
		neutralPlanet1.ownerId = 0;
		neutralPlanet1.position = new Vector2(-200, 0);
		neutralPlanet1.startArmorPercent = 0.2f;
		planets.add(neutralPlanet1);
		
		PlanetInitialization neutralPlanet2 = new PlanetInitialization();
		neutralPlanet2.ownerId = 0;
		neutralPlanet2.position = new Vector2(200, 0);
		neutralPlanet2.startArmorPercent = 0.2f;
		planets.add(neutralPlanet2);*/
		
		PlanetInitialization ancientPlanet = new PlanetInitialization();
		ancientPlanet.setAncient(true);
		ancientPlanet.setOwnerId(0);
		ancientPlanet.setPosition(new Vector2(0, 0));
		planets.add(ancientPlanet);
		
		PlanetInitialization startPlanetPlayer1 = new PlanetInitialization();
		startPlanetPlayer1.setOwnerId(1);
		startPlanetPlayer1.setPosition(new Vector2(0, -400));
		planets.add(startPlanetPlayer1);
		
		PlanetInitialization startPlanetAI1 = new PlanetInitialization();
		startPlanetAI1.setOwnerId(4);
		startPlanetAI1.setPosition(new Vector2(0, 400));
		planets.add(startPlanetAI1);
		
		if (playerCount > 1) {
			PlanetInitialization startPlanetPlayer2 = new PlanetInitialization();
			startPlanetPlayer2.setOwnerId(2);
			startPlanetPlayer2.setPosition(new Vector2(-200, -400));
			planets.add(startPlanetPlayer2);
			
			PlanetInitialization startPlanetAI2 = new PlanetInitialization();
			startPlanetAI2.setOwnerId(5);
			startPlanetAI2.setPosition(new Vector2(200, 400));
			planets.add(startPlanetAI2);
		}
		
		if (playerCount > 2) {
			PlanetInitialization startPlanetPlayer3 = new PlanetInitialization();
			startPlanetPlayer3.setOwnerId(3);
			startPlanetPlayer3.setPosition(new Vector2(200, -400));
			planets.add(startPlanetPlayer3);
			
			PlanetInitialization startPlanetAI3 = new PlanetInitialization();
			startPlanetAI3.setOwnerId(6);
			startPlanetAI3.setPosition(new Vector2(-200, 400));
			planets.add(startPlanetAI3);
		}
		map.setPlanets(planets);
		return map;
	}

}
