package de.instinct.engine.tools.devrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.FleetEngine;
import de.instinct.engine.ai.AiDifficulty;
import de.instinct.engine.ai.AiEngine;
import de.instinct.engine.initialization.GameStateInitialization;
import de.instinct.engine.initialization.PlanetInitialization;
import de.instinct.engine.map.GameMap;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.PlanetData;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.ShipType;
import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.model.ship.WeaponType;
import de.instinct.engine.order.GameOrder;

public class TestEngineManager {
	
	public static GameState state;
	
	private static FleetEngine engine;
	private static AiEngine aiEngine;
	
	public static void init() {
		aiEngine = new AiEngine();
		engine = new FleetEngine();
		engine.initialize();
		state = engine.initializeGameState(getGameStateInitialization());
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
	}
	
	public static void queue(GameOrder order) {
		engine.queue(state, order);
	}
	
	private static GameStateInitialization getGameStateInitialization() {
		GameStateInitialization init = new GameStateInitialization();
		init.gameUUID = UUID.randomUUID().toString();
		init.players = initializePlayers(3);
		init.map = initializeMap(3);
		init.ancientPlanetResourceDegradationFactor = 0.5f;
		init.gameTimeLimitMS = 180_000;
		init.atpToWin = 50;
		return init;
	}

	private static List<Player> initializePlayers(int playerCount) {
		List<Player> players= new ArrayList<>();
		Player neutralPlayer = new Player();
		neutralPlayer.id = 0;
		neutralPlayer.teamId = 0;
		neutralPlayer.name = "Neutral Player";
		neutralPlayer.commandPointsGenerationSpeed = 0;
		neutralPlayer.startCommandPoints = 0;
		neutralPlayer.maxCommandPoints = 0;
		PlanetData neutralPlanetData = new PlanetData();
		neutralPlanetData.resourceGenerationSpeed = 0;
		neutralPlanetData.maxResourceCapacity = 0;
		neutralPlanetData.percentOfArmorAfterCapture = 0;
		Weapon neutralPlanetWeapon = new Weapon();
		neutralPlanetWeapon.type = WeaponType.MISSILE;
		neutralPlanetWeapon.damage = 5;
		neutralPlanetWeapon.range = 100f;
		neutralPlanetWeapon.cooldown = 1000;
		neutralPlanetWeapon.speed = 80f;
		neutralPlanetData.weapon = neutralPlanetWeapon;
		Defense neutralPlanetDefense = new Defense();
		neutralPlanetDefense.shield = 0;
		neutralPlanetDefense.armor = 50;
		neutralPlanetDefense.shieldRegenerationSpeed = 0;
		neutralPlanetData.defense = neutralPlanetDefense;
		neutralPlayer.planetData = neutralPlanetData;
		neutralPlayer.ships = new ArrayList<>();
		players.add(neutralPlayer);
		
		Player player = createPlayer();
		player.id = 1;
		player.teamId = 1;
		player.name = "Player 1";
		players.add(player);
		
		AiEngine aiEngine = new AiEngine();
		Player aiPlayer = aiEngine.initialize(AiDifficulty.RETARDED);
		aiPlayer.id = 4;
		aiPlayer.teamId = 2;
		players.add(aiPlayer);
		
		if (playerCount > 1) {
			Player player2 = createPlayer();
			player2.id = 2;
			player2.teamId = 1;
			player2.name = "Player 2";
			players.add(player2);
			
			Player aiPlayer2 = aiEngine.initialize(AiDifficulty.RETARDED);
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
			
			Player aiPlayer3 = aiEngine.initialize(AiDifficulty.RETARDED);
			aiPlayer3.id = 6;
			aiPlayer3.teamId = 2;
			players.add(aiPlayer3);
		}
		return players;
	}
	
	private static Player createPlayer() {
		Player player = new Player();
		player.commandPointsGenerationSpeed = 0.1;
		player.startCommandPoints = 3;
		player.maxCommandPoints = 10;
		PlanetData playerPlanetData = new PlanetData();
		playerPlanetData.resourceGenerationSpeed = 1f;
		playerPlanetData.maxResourceCapacity = 20;
		playerPlanetData.percentOfArmorAfterCapture = 0.2f;
		Weapon playerPlanetWeapon = new Weapon();
		playerPlanetWeapon.type = WeaponType.PROJECTILE;
		playerPlanetWeapon.damage = 5;
		playerPlanetWeapon.range = 100f;
		playerPlanetWeapon.cooldown = 1000;
		playerPlanetWeapon.speed = 150f;
		playerPlanetData.weapon = playerPlanetWeapon;
		Defense playerPlanetDefense = new Defense();
		playerPlanetDefense.shield = 10;
		playerPlanetDefense.armor = 20;
		playerPlanetDefense.shieldRegenerationSpeed = 0.5f;
		playerPlanetData.defense = playerPlanetDefense;
		player.planetData = playerPlanetData;
		player.ships = new ArrayList<>();
		
		ShipData playerShipHawk = new ShipData();
		playerShipHawk.model = "hawk";
		playerShipHawk.type = ShipType.FIGHTER;
		playerShipHawk.movementSpeed = 60;
		playerShipHawk.cost = 2;
		playerShipHawk.commandPointsCost = 1;
		Weapon playerShipWeaponHawk = new Weapon();
		playerShipWeaponHawk.type = WeaponType.LASER;
		playerShipWeaponHawk.damage = 5;
		playerShipWeaponHawk.range = 30f;
		playerShipWeaponHawk.cooldown = 2000;
		playerShipWeaponHawk.speed = 80f;
		playerShipHawk.weapon = playerShipWeaponHawk;
		Defense playerShipDefenseHawk = new Defense();
		playerShipDefenseHawk.shield = 6;
		playerShipDefenseHawk.armor = 15;
		playerShipDefenseHawk.shieldRegenerationSpeed = 0.1f;
		playerShipHawk.defense = playerShipDefenseHawk;
		player.ships.add(playerShipHawk);
		
		ShipData playerShipTurtle = new ShipData();
		playerShipTurtle.model = "turtle";
		playerShipTurtle.type = ShipType.FIGHTER;
		playerShipTurtle.movementSpeed = 100;
		playerShipTurtle.cost = 3;
		playerShipTurtle.commandPointsCost = 1;
		Weapon playerShipWeaponTurtle = new Weapon();
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
		return player;
	}

	private static GameMap initializeMap(int playerCount) {
		GameMap map = new GameMap();
		List<PlanetInitialization> planets = new ArrayList<>();
		PlanetInitialization neutralPlanet1 = new PlanetInitialization();
		neutralPlanet1.ownerId = 0;
		neutralPlanet1.position = new Vector2(-200, 0);
		neutralPlanet1.startArmorPercent = 0.2f;
		planets.add(neutralPlanet1);
		
		PlanetInitialization neutralPlanet2 = new PlanetInitialization();
		neutralPlanet2.ownerId = 0;
		neutralPlanet2.position = new Vector2(200, 0);
		neutralPlanet2.startArmorPercent = 0.2f;
		planets.add(neutralPlanet2);
		
		PlanetInitialization ancientPlanet = new PlanetInitialization();
		ancientPlanet.ancient = true;
		ancientPlanet.ownerId = 0;
		ancientPlanet.position = new Vector2(0, 0);
		ancientPlanet.startArmorPercent = 0f;
		planets.add(ancientPlanet);
		
		PlanetInitialization startPlanetPlayer1 = new PlanetInitialization();
		startPlanetPlayer1.ownerId = 1;
		startPlanetPlayer1.position = new Vector2(0, -400);
		startPlanetPlayer1.startArmorPercent = 1f;
		planets.add(startPlanetPlayer1);
		
		PlanetInitialization startPlanetAI1 = new PlanetInitialization();
		startPlanetAI1.ownerId = 4;
		startPlanetAI1.position = new Vector2(0, 400);
		startPlanetAI1.startArmorPercent = 1f;
		planets.add(startPlanetAI1);
		
		if (playerCount > 1) {
			PlanetInitialization startPlanetPlayer2 = new PlanetInitialization();
			startPlanetPlayer2.ownerId = 2;
			startPlanetPlayer2.position = new Vector2(-200, -400);
			startPlanetPlayer2.startArmorPercent = 1f;
			planets.add(startPlanetPlayer2);
			
			PlanetInitialization startPlanetAI2 = new PlanetInitialization();
			startPlanetAI2.ownerId = 5;
			startPlanetAI2.position = new Vector2(200, 400);
			startPlanetAI2.startArmorPercent = 1f;
			planets.add(startPlanetAI2);
		}
		
		if (playerCount > 2) {
			PlanetInitialization startPlanetPlayer3 = new PlanetInitialization();
			startPlanetPlayer3.ownerId = 3;
			startPlanetPlayer3.position = new Vector2(200, -400);
			startPlanetPlayer3.startArmorPercent = 1f;
			planets.add(startPlanetPlayer3);
			
			PlanetInitialization startPlanetAI3 = new PlanetInitialization();
			startPlanetAI3.ownerId = 6;
			startPlanetAI3.position = new Vector2(-200, 400);
			startPlanetAI3.startArmorPercent = 1f;
			planets.add(startPlanetAI3);
		}
		map.planets = planets;
		return map;
	}

}
