package de.instinct.eqfleet.game.backend.driver.local.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.math.Vector2;

import de.instinct.api.construction.dto.Infrastructure;
import de.instinct.api.construction.dto.PlanetDefense;
import de.instinct.api.construction.dto.PlanetWeapon;
import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.api.matchmaking.model.VersusMode;
import de.instinct.api.meta.dto.LoadoutData;
import de.instinct.api.shipyard.dto.ShipBlueprint;
import de.instinct.api.shipyard.dto.ShipDefense;
import de.instinct.api.shipyard.dto.ShipWeapon;
import de.instinct.engine.ai.AiDifficulty;
import de.instinct.engine.ai.AiEngine;
import de.instinct.engine.initialization.GameStateInitialization;
import de.instinct.engine.initialization.PlanetInitialization;
import de.instinct.engine.map.GameMap;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.PlanetData;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.ShipType;
import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.model.ship.WeaponType;
import de.instinct.engine.util.EngineUtility;

public class CustomLoader {

	private AiEngine aiEngine;
	
	public CustomLoader() {
		aiEngine = new AiEngine();
	}
	
	public GameStateInitialization generateInitialGameState(LoadoutData loadout) {
		GameStateInitialization initialization = new GameStateInitialization();
		initialization.gameUUID = UUID.randomUUID().toString();
		initialization.players = loadPlayers(loadout);
		initialization.map = generateMap(GameType.builder()
				.factionMode(FactionMode.ONE_VS_ONE)
				.gameMode(GameMode.CONQUEST)
				.versusMode(VersusMode.AI)
				.build());
		initialization.ancientPlanetResourceDegradationFactor = 0.5f;
		initialization.gameTimeLimitMS = 180_000;
		initialization.atpToWin = 50;
		return initialization;
	}

	public List<Player> loadPlayers(LoadoutData loadout) {
		List<Player> players = new ArrayList<>();
		
		Player neutralPlayer = new Player();
		neutralPlayer.id = 0;
		neutralPlayer.teamId = 0;
		neutralPlayer.name = "Neutral Player";
		PlanetData neutralPlanetData = new PlanetData();
		Weapon neutralPlanetWeapon = new Weapon();
		neutralPlanetWeapon.type = WeaponType.PROJECTILE;
		neutralPlanetWeapon.damage = 5;
		neutralPlanetWeapon.range = 100f; 
		neutralPlanetWeapon.cooldown = 1000;
		neutralPlanetWeapon.speed = 120f;
		neutralPlanetData.weapon = neutralPlanetWeapon;
		Defense neutralPlanetDefense = new Defense();
		neutralPlanetDefense.armor = 50;
		neutralPlanetData.defense = neutralPlanetDefense;
		neutralPlayer.planetData = neutralPlanetData;
		neutralPlayer.ships = new ArrayList<>();
		players.add(neutralPlayer);
		
		Player userPlayer = getPlayer(loadout);
		userPlayer.id = 1;
		userPlayer.teamId = 1;
		players.add(userPlayer);
		
		AiPlayer aiPlayer = aiEngine.initialize(AiDifficulty.RETARDED);
		aiPlayer.id = 4;
		aiPlayer.teamId = 2;
		players.add(aiPlayer);
		
		return players;
	}
	
	private Player getPlayer(LoadoutData loadout) {
		Player newPlayer = new Player();
		newPlayer.commandPointsGenerationSpeed = loadout.getCommandPointsGenerationSpeed();
		newPlayer.startCommandPoints = loadout.getStartCommandPoints();
		newPlayer.maxCommandPoints = loadout.getMaxCommandPoints();
		newPlayer.planetData = getPlanetData(loadout);
		newPlayer.ships = getShips(loadout);
		return newPlayer;
	}

	private PlanetData getPlanetData(LoadoutData loadout) {
		Infrastructure infrastructure = loadout.getInfrastructure();
		if (loadout.getInfrastructure() != null) {
			PlanetData planetData = new PlanetData();
			planetData.maxResourceCapacity = infrastructure.getMaxResourceCapacity();
			planetData.resourceGenerationSpeed = infrastructure.getResourceGenerationSpeed();
			planetData.percentOfArmorAfterCapture = infrastructure.getPercentOfArmorAfterCapture();
			planetData.defense = getDefense(infrastructure.getPlanetDefense());
			planetData.weapon = getWeapon(infrastructure.getPlanetWeapon());
			return planetData;
		}
		return null;
	}

	private List<ShipData> getShips(LoadoutData loadout) {
		List<ShipData> ships = new ArrayList<>();
		for (ShipBlueprint userShip : loadout.getShips()) {
			ShipData shipData = new ShipData();
			shipData.type = ShipType.valueOf(userShip.getType().toString());
			shipData.cost = userShip.getCost();
			shipData.model = userShip.getModel();
			shipData.movementSpeed = userShip.getMovementSpeed();
			shipData.commandPointsCost = userShip.getCommandPointsCost();
			shipData.weapon = getWeapon(userShip.getWeapon());
			shipData.defense = getDefense(userShip.getDefense());
			ships.add(shipData);
		}
		return ships;
	}
	
	private Defense getDefense(ShipDefense shipDefense) {
		Defense defense = new Defense();
		defense.armor = shipDefense.getArmor();
		defense.shield = shipDefense.getShield();
		defense.shieldRegenerationSpeed = shipDefense.getShieldRegenerationSpeed();
		return defense;
	}

	private Weapon getWeapon(ShipWeapon shipWeapon) {
		Weapon weapon = new Weapon();
		weapon.type = WeaponType.valueOf(shipWeapon.getType().toString());
		weapon.damage = shipWeapon.getDamage();
		weapon.range = shipWeapon.getRange();
		weapon.cooldown = shipWeapon.getCooldown();
		weapon.speed = shipWeapon.getSpeed();
		return weapon;
	}

	private Weapon getWeapon(PlanetWeapon planetWeapon) {
		Weapon weapon = new Weapon();
		weapon.type = WeaponType.valueOf(planetWeapon.getType().toString());
		weapon.damage = planetWeapon.getDamage();
		weapon.range = planetWeapon.getRange();
		weapon.cooldown = planetWeapon.getCooldown();
		weapon.speed = planetWeapon.getSpeed();
		return weapon;
	}

	private Defense getDefense(PlanetDefense planetDefense) {
		Defense defense = new Defense();
		defense.armor = planetDefense.getArmor();
		defense.shield = planetDefense.getShield();
		defense.shieldRegenerationSpeed = planetDefense.getShieldRegenerationSpeed();
		return defense;
	}

	private GameMap generateMap(GameType gameType) {
		GameMap map = new GameMap();
		List<PlanetInitialization> planets = new ArrayList<>();
		generateAncientPlanet(planets);
		generateNeutralPlanets(planets);
		generatePlayerPlanets(planets, gameType);
		map.planets = planets;
		return map;
	}

	private void generatePlayerPlanets(List<PlanetInitialization> planets, GameType gameType) {
		PlanetInitialization startPlanetPlayerOne = new PlanetInitialization();
    	startPlanetPlayerOne.ownerId = 1;
    	startPlanetPlayerOne.position = new Vector2(0, -(EngineUtility.MAP_BOUNDS.y / 2) + 300);
    	startPlanetPlayerOne.startArmorPercent = 0.5f;
    	planets.add(startPlanetPlayerOne);
    	
    	PlanetInitialization startPlanetPlayerTwo = new PlanetInitialization();
    	startPlanetPlayerTwo.ownerId = 4;
    	startPlanetPlayerTwo.position = new Vector2(0, (EngineUtility.MAP_BOUNDS.y / 2) - 300);
    	startPlanetPlayerTwo.startArmorPercent = 0.5f;
    	planets.add(startPlanetPlayerTwo);
    	
    	if (gameType.factionMode.teamPlayerCount >= 2) {
    		PlanetInitialization startPlanetPlayerThree = new PlanetInitialization();
    		startPlanetPlayerThree.ownerId = 2;
    		startPlanetPlayerThree.position = new Vector2(-200, (EngineUtility.MAP_BOUNDS.y / 2) + 300);
    		startPlanetPlayerThree.startArmorPercent = 0.5f;
        	planets.add(startPlanetPlayerThree);
        	
        	PlanetInitialization startPlanetPlayerFour = new PlanetInitialization();
        	startPlanetPlayerFour.ownerId = 5;
        	startPlanetPlayerFour.position = new Vector2(200, (EngineUtility.MAP_BOUNDS.y / 2) - 300);
        	startPlanetPlayerFour.startArmorPercent = 0.5f;
        	planets.add(startPlanetPlayerFour);
		}
    	
    	if (gameType.factionMode.teamPlayerCount >= 3) {
    		PlanetInitialization startPlanetPlayerFive = new PlanetInitialization();
    		startPlanetPlayerFive.ownerId = 3;
    		startPlanetPlayerFive.position = new Vector2(200, -(EngineUtility.MAP_BOUNDS.y / 2) + 300);
    		startPlanetPlayerFive.startArmorPercent = 0.5f;
        	planets.add(startPlanetPlayerFive);
        	
        	PlanetInitialization startPlanetPlayerSix = new PlanetInitialization();
        	startPlanetPlayerSix.ownerId = 6;
        	startPlanetPlayerSix.position = new Vector2(-200, (EngineUtility.MAP_BOUNDS.y / 2) - 300);
        	startPlanetPlayerSix.startArmorPercent = 0.5f;
        	planets.add(startPlanetPlayerSix);
		}
	}

	private void generateNeutralPlanets(List<PlanetInitialization> planets) {
		PlanetInitialization neutralPlanet2 = new PlanetInitialization();
    	neutralPlanet2.ownerId = 0;
    	neutralPlanet2.position = new Vector2(0, -250);
    	neutralPlanet2.startArmorPercent = 0.3f;
    	planets.add(neutralPlanet2);
    	
    	PlanetInitialization neutralPlanet3 = new PlanetInitialization();
    	neutralPlanet3.ownerId = 0;
    	neutralPlanet3.position = new Vector2(0, 250);
    	neutralPlanet3.startArmorPercent = 0.3f;
    	planets.add(neutralPlanet3);
    	
    	PlanetInitialization neutralPlanet4 = new PlanetInitialization();
    	neutralPlanet4.ownerId = 0;
    	neutralPlanet4.position = new Vector2(250, 0);
    	neutralPlanet4.startArmorPercent = 0.2f;
    	planets.add(neutralPlanet4);
    	
    	PlanetInitialization neutralPlanet5 = new PlanetInitialization();
    	neutralPlanet5.ownerId = 0;
    	neutralPlanet5.position = new Vector2(-250, 0);
    	neutralPlanet5.startArmorPercent = 0.2f;
    	planets.add(neutralPlanet5);
    	
    	PlanetInitialization neutralPlanet6 = new PlanetInitialization();
    	neutralPlanet6.ownerId = 0;
    	neutralPlanet6.position = new Vector2(250, 400);
    	neutralPlanet6.startArmorPercent = 0.1f;
    	planets.add(neutralPlanet6);
    	
    	PlanetInitialization neutralPlanet7 = new PlanetInitialization();
    	neutralPlanet7.ownerId = 0;
    	neutralPlanet7.position = new Vector2(-250, -400);
    	neutralPlanet7.startArmorPercent = 0.1f;
    	planets.add(neutralPlanet7);
	}

	private void generateAncientPlanet(List<PlanetInitialization> planets) {
		PlanetInitialization ancientPlanet = new PlanetInitialization();
    	ancientPlanet.ownerId = 0;
    	ancientPlanet.position = new Vector2(0, 0);
    	ancientPlanet.startArmorPercent = 0f;
    	ancientPlanet.ancient = true;
    	planets.add(ancientPlanet);
	}

}
