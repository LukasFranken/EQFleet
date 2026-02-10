package de.instinct.eqfleet.game.backend.driver.local.tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.AiPlayer;
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
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine_api.ai.service.AIPlayerLoader;
import de.instinct.engine_api.core.model.GameMap;
import de.instinct.engine_api.core.model.GameStateInitialization;
import de.instinct.engine_api.core.model.PlanetInitialization;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.ActionBehavior;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.Condition;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes.CameraMoveGuideEvent;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes.DialogGuideEvent;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.VerticalAlignment;

public class TutorialLoader {

	private AIPlayerLoader aiPlayerLoader;
	private DefaultGuideEventLoader eventLoader;

	public TutorialLoader() {
		aiPlayerLoader = new AIPlayerLoader();
		eventLoader = new DefaultGuideEventLoader();
	}

	public GameStateInitialization generateInitialGameState() {
		GameStateInitialization initialGameState = new GameStateInitialization();
		initialGameState.setGameUUID("tutorial");
		initialGameState.setPlayers(loadPlayers());
		initialGameState.setAncientPlanetResourceDegradationFactor(0.25f);;
		initialGameState.setGameTimeLimitMS(240_000);
		initialGameState.setAtpToWin(30);
		initialGameState.setPauseTimeLimitMS(20_000);
		initialGameState.setPauseCountLimit(0);
		initialGameState.setMap(generateMap());
		return initialGameState;
	}

	private List<Player> loadPlayers() {
		List<Player> players = new ArrayList<>();
		
		Player neutralPlayer = new Player();
		neutralPlayer.id = 0;
		neutralPlayer.teamId = 0;
		neutralPlayer.name = "Neutral Player";
		PlanetData neutralPlanetData = new PlanetData();
		neutralPlayer.planetData = neutralPlanetData;
		neutralPlayer.ships = new ArrayList<>();
		neutralPlayer.turrets = new ArrayList<>();
		players.add(neutralPlayer);
		
		Player player = new Player();
		player.id = 1;
		player.teamId = 1;
		player.name = "Player 1";
		player.maxCommandPoints = 10;
		player.startCommandPoints = 1;
		player.commandPointsGenerationSpeed = 0.2;
		player.currentCommandPoints = player.startCommandPoints;
		
		PlanetData tutorialPlanetData = new PlanetData();
		tutorialPlanetData.resourceGenerationSpeed = 0.3f;
		tutorialPlanetData.maxResourceCapacity = 10;
		player.planetData = tutorialPlanetData;
		players.add(player);
		
		player.ships = new ArrayList<>();
		ShipData tutorialShip = new ShipData();
		tutorialShip.model = "hawk";
		tutorialShip.cpCost = 1;
		tutorialShip.resourceCost = 3;
		
		CoreData tutorialShipCore = new CoreData();
		tutorialShipCore.type = CoreType.FIGHTER;
		tutorialShip.core = tutorialShipCore;
		
		EngineData tutorialShipEngine = new EngineData();
		tutorialShipEngine.type = EngineType.ION;
		tutorialShipEngine.speed = 100;
		tutorialShipEngine.acceleration = 1f;
		tutorialShip.engine = tutorialShipEngine;
		
		HullData tutorialShipHull = new HullData();
		tutorialShipHull.strength = 9f;
		tutorialShip.hull = tutorialShipHull;
		
		tutorialShip.weapons = new ArrayList<>();
		WeaponData tutorialShipWeapon = new WeaponData();
		tutorialShipWeapon.type = WeaponType.PROJECTILE;
		tutorialShipWeapon.damage = 1;
		tutorialShipWeapon.range = 80f;
		tutorialShipWeapon.cooldown = 500;
		tutorialShipWeapon.speed = 120f;
		tutorialShip.weapons.add(tutorialShipWeapon);
		
		tutorialShip.shields = new ArrayList<>();
		ShieldData tutorialShipShield = new ShieldData();
		tutorialShipShield.type = ShieldType.PLASMA;
		tutorialShipShield.strength = 2f;
		tutorialShipShield.generation = 0.2f;
		tutorialShip.shields.add(tutorialShipShield);
		player.ships.add(tutorialShip);
		player.turrets = new ArrayList<>();
		
		//------------------------------------------

		AiPlayer aiPlayer = aiPlayerLoader.initialize(1);
		aiPlayer.id = 2;
		aiPlayer.teamId = 2;
		aiPlayer.turrets = new ArrayList<>();
		aiPlayer.ships.get(0).weapons.get(0).damage = 1;
		aiPlayer.ships.get(0).hull.strength = 4;
		aiPlayer.ships.get(0).engine.speed = 100f;
		
		ShipData aiSmartassShip = new ShipData();
		aiSmartassShip.model = "hawk";
		aiSmartassShip.cpCost = 1;
		aiSmartassShip.resourceCost = 3;
		
		CoreData aiSmartassShipCore = new CoreData();
		aiSmartassShipCore.type = CoreType.FIGHTER;
		aiSmartassShip.core = aiSmartassShipCore;
		
		EngineData aiSmartassShipEngine = new EngineData();
		aiSmartassShipEngine.type = EngineType.ION;
		aiSmartassShipEngine.speed = 100;
		aiSmartassShipEngine.acceleration = 1f;
		aiSmartassShip.engine = aiSmartassShipEngine;
		
		HullData aiSmartassShipHull = new HullData();
		aiSmartassShipHull.strength = 20f;
		aiSmartassShip.hull = aiSmartassShipHull;
		
		aiSmartassShip.weapons = new ArrayList<>();
		WeaponData aiSmartassShipWeapon = new WeaponData();
		aiSmartassShipWeapon.type = WeaponType.LASER;
		aiSmartassShipWeapon.damage = 12;
		aiSmartassShipWeapon.range = 100f;
		aiSmartassShipWeapon.cooldown = 500;
		aiSmartassShipWeapon.speed = 220f;
		aiSmartassShip.weapons.add(aiSmartassShipWeapon);
		
		aiSmartassShip.shields = new ArrayList<>();
		ShieldData aiSmartassShipShield = new ShieldData();
		aiSmartassShipShield.type = ShieldType.PLASMA;
		aiSmartassShipShield.strength = 20f;
		aiSmartassShipShield.generation = 0.2f;
		aiSmartassShip.shields.add(aiSmartassShipShield);
		aiPlayer.ships.add(aiSmartassShip);
		aiPlayer.turrets = new ArrayList<>();
		players.add(aiPlayer);

		return players;
	}

	private GameMap generateMap() {
		GameMap map = new GameMap();
		map.setZoomFactor(1f);
		List<PlanetInitialization> planets = new ArrayList<>();
		PlanetInitialization startPlanetPlayerOne = new PlanetInitialization();
		startPlanetPlayerOne.setOwnerId(1);
		startPlanetPlayerOne.setPosition(new Vector2(0, -(EngineUtility.MAP_BOUNDS.y / 2) + EngineUtility.PLANET_RADIUS + 150));
    	planets.add(startPlanetPlayerOne);
    	
    	PlanetInitialization startPlanetPlayerTwo = new PlanetInitialization();
    	startPlanetPlayerTwo.setOwnerId(2);
    	startPlanetPlayerTwo.setPosition(new Vector2(350, -(EngineUtility.MAP_BOUNDS.y / 2) + EngineUtility.PLANET_RADIUS + 150));
    	planets.add(startPlanetPlayerTwo);
    	
    	PlanetInitialization ancientPlanet = new PlanetInitialization();
    	ancientPlanet.setOwnerId(0);
    	ancientPlanet.setPosition(new Vector2(0, 0));
    	ancientPlanet.setAncient(true);
    	planets.add(ancientPlanet);
    	
    	PlanetInitialization neutralPlanet1 = new PlanetInitialization();
    	neutralPlanet1.setOwnerId(0);
    	neutralPlanet1.setPosition(new Vector2(400, -400));
    	planets.add(neutralPlanet1);
    	
    	PlanetInitialization neutralPlanet2 = new PlanetInitialization();
    	neutralPlanet2.setOwnerId(0);
    	neutralPlanet2.setPosition(new Vector2(-150, -500));
    	planets.add(neutralPlanet2);
		map.setPlanets(planets);
		return map;
	}

	public Queue<GuideEvent> load(TutorialMode mode) {
		Queue<GuideEvent> guideQueue = new ConcurrentLinkedQueue<>();

		DialogGuideEvent setupGuideEvent = new DialogGuideEvent();
		setupGuideEvent.setDuration(0f);
		setupGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				GameModel.inputEnabled = false;
				Game.setUIElementVisible("ownCP", false);
				Game.setUIElementVisible("enemy1CP", false);
				Game.setUIElementVisible("teamAP", false);
				Game.setUIElementVisible("enemyAP", false);
				Game.setUIElementVisible("time", false);
				AudioManager.updateUserVoiceVolume(0.7f);
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(setupGuideEvent);
		
		guideQueue.add(eventLoader.pause(1f));
		guideQueue.add(eventLoader.dialog(1, VerticalAlignment.CENTER));
		if (mode == TutorialMode.FULL) {
			guideQueue.add(eventLoader.dialog(2, VerticalAlignment.CENTER));
			guideQueue.add(eventLoader.dialog(3, VerticalAlignment.CENTER));
			guideQueue.add(eventLoader.dialog(4, VerticalAlignment.CENTER));
			guideQueue.add(eventLoader.pause(1f));
			guideQueue.add(eventLoader.dialog(5, VerticalAlignment.CENTER));
			guideQueue.add(eventLoader.dialog(6, VerticalAlignment.CENTER));
		}
		guideQueue.add(eventLoader.dialog(7, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				GameModel.visible = true;
				AudioManager.playMusic("to_the_stars_ambient", true);
				AudioManager.startRadio();
			}
			
		}));
		guideQueue.add(eventLoader.pause(2f));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				Game.setUIElementVisible("time", true);
			}
			
		}));
		guideQueue.add(eventLoader.dialog(8, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.pan(2f, new Vector3(0f, -800f, 2000f)));
		guideQueue.add(eventLoader.dialog(9, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.dialog(10, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.pause(1f));
		guideQueue.add(eventLoader.pan(2f, new Vector3(-50f, -700f, 2000f)));
		if (mode == TutorialMode.FULL) {
			guideQueue.add(eventLoader.dialog(11, VerticalAlignment.TOP));
		}
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				Game.setUIElementVisible("ownCP", true);
				GameModel.inputEnabled = true;
			}
			
		}));
		guideQueue.add(eventLoader.dialog(12, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.condition(new Condition() {
			
			@Override
			public boolean isMet() {
				for (Ship ship : GameModel.activeGameState.ships) {
					if (ship.ownerId == 1) {
						return true;
					}
				}
				return false;
			}
			
		}));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				GameModel.inputEnabled = false;
			}
			
		}));
		guideQueue.add(eventLoader.pause(1f));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				Game.pause();
			}
			
		}));
		if (mode == TutorialMode.FULL) {
			guideQueue.add(eventLoader.dialog(13, VerticalAlignment.TOP));
		}
		guideQueue.add(eventLoader.dialog(14, VerticalAlignment.TOP));
		if (mode == TutorialMode.FULL) {
			guideQueue.add(eventLoader.dialog(15, VerticalAlignment.TOP));
		}
		guideQueue.add(eventLoader.dialog(16, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				Game.unpause();
			}
			
		}));
		guideQueue.add(eventLoader.condition(new Condition() {
			
			@Override
			public boolean isMet() {
				for (Ship ship : GameModel.activeGameState.ships) {
					if (ship.ownerId == 1) {
						return false;
					}
				}
				return true;
			}
			
		}));
		if (mode == TutorialMode.FULL) {
			guideQueue.add(eventLoader.dialog(17, VerticalAlignment.TOP));
		}
		guideQueue.add(eventLoader.pan(2f, new Vector3(130f, -700f, 3000f)));
		guideQueue.add(eventLoader.pan(2f, new Vector3(320f, -700f, 2300f)));
		guideQueue.add(eventLoader.dialog(18, VerticalAlignment.TOP));
		
		if (mode == TutorialMode.TOO_MUCH) {
			guideQueue.add(eventLoader.dialog(19, VerticalAlignment.TOP));
		}
		if (mode == TutorialMode.FULL) {
			guideQueue.add(eventLoader.dialog(20, VerticalAlignment.TOP));
		}
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				Game.setUIElementVisible("enemy1CP", true);
			}
			
		}));
		guideQueue.add(eventLoader.dialog(21, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				FleetMovementMessage aiFleetMoveMessage = new FleetMovementMessage();
				aiFleetMoveMessage.userUUID = "2";
				aiFleetMoveMessage.fromPlanetId = 1;
				aiFleetMoveMessage.toPlanetId = 3;
				GameModel.outputMessageQueue.add(aiFleetMoveMessage);
			}
			
		}));
		guideQueue.add(eventLoader.pan(3.5f, new Vector3(130f, -700f, 3000f)));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				GameModel.inputEnabled = true;
			}
			
		}));
		guideQueue.add(eventLoader.dialog(22, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.condition(new Condition() {
			
			@Override
			public boolean isMet() {
				for (Ship ship : GameModel.activeGameState.ships) {
					if (ship.ownerId == 1) {
						if (ship.targetPlanetId == 3 || ship.targetPlanetId == 1) {
							return true;
						}
					}
				}
				return false;
			}
			
		}));
		CameraMoveGuideEvent panToShipGuideEvent = new CameraMoveGuideEvent();
		panToShipGuideEvent.setDuration(2f);
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				GameModel.inputEnabled = false;
				int originId = 0;
				int targetId = 3;
				for (Ship ship : GameModel.activeGameState.ships) {
					if (ship.ownerId == 1) {
						originId = ship.originPlanetId;
						targetId = ship.targetPlanetId;
						if (targetId == 3) {
							panToShipGuideEvent.setTargetCameraPos(new Vector3(300f, -450f, 1300f));
						} else {
							panToShipGuideEvent.setTargetCameraPos(new Vector3(270f, -660f, 1300f));
						}
					}
				}
				FleetMovementMessage aiFleetMoveMessage = new FleetMovementMessage();
				aiFleetMoveMessage.userUUID = "2";
				aiFleetMoveMessage.fromPlanetId = targetId;
				aiFleetMoveMessage.toPlanetId = originId;
				GameModel.outputMessageQueue.add(aiFleetMoveMessage);
			}
			
		}));
		guideQueue.add(eventLoader.pause(0.5f));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				Game.pause();
			}
			
		}));
		guideQueue.add(eventLoader.dialog(23, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.dialog(24, VerticalAlignment.TOP));
		guideQueue.add(panToShipGuideEvent);
		guideQueue.add(eventLoader.dialog(25, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.pan(2f, new Vector3(200f, -600f, 1800f)));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				Game.unpause();
			}
			
		}));
		guideQueue.add(eventLoader.condition(new Condition() {
			
			@Override
			public boolean isMet() {
				return EngineUtility.getPlanet(GameModel.activeGameState.planets, 3).ownerId == 1 || EngineUtility.getPlanet(GameModel.activeGameState.planets, 1).ownerId == 1;
			}
			
		}));
		guideQueue.add(eventLoader.dialog(26, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.pan(2f, new Vector3(0f, 0f, 2000f)));
		guideQueue.add(eventLoader.dialog(27, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.pan(2f, new Vector3(130f, -200f, 3800f)));
		guideQueue.add(eventLoader.dialog(28, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				GameModel.inputEnabled = true;
			}
			
		}));
		guideQueue.add(eventLoader.dialog(29, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.condition(new Condition() {
			
			@Override
			public boolean isMet() {
				return EngineUtility.getPlanet(GameModel.activeGameState.planets, 2).ownerId == 1;
			}
			
		}));
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				GameModel.inputEnabled = false;
				Game.setUIElementVisible("teamAP", true);
				Game.setUIElementVisible("enemyAP", true);
			}
			
		}));
		guideQueue.add(eventLoader.dialog(30, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.dialog(31, VerticalAlignment.TOP));
		if (mode == TutorialMode.FULL) {
			guideQueue.add(eventLoader.dialog(32, VerticalAlignment.TOP));
		}
		guideQueue.add(eventLoader.action(new Action() {
			
			@Override
			public void execute() {
				GameModel.inputEnabled = true;
				GameModel.activeGameState.maxGameTimeMS = GameModel.activeGameState.gameTimeMS + 30_000;
				AudioManager.updateUserVoiceVolume(0.5f);
			}
			
		}));
		guideQueue.add(eventLoader.dialog(33, VerticalAlignment.TOP));
		guideQueue.add(eventLoader.condition(new Condition() {
			
			@Override
			public boolean isMet() {
				return GameModel.activeGameState.winner == 1;
			}
			
		}));
		return guideQueue;
	}

}
