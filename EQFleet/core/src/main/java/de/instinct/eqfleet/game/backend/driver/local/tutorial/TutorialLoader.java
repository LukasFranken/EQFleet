package de.instinct.eqfleet.game.backend.driver.local.tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
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
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.ConditionalBehavior;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.MessageBehavior;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes.CameraMoveGuideEvent;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes.DialogGuideEvent;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes.PauseGuideEvent;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.VerticalAlignment;

public class TutorialLoader {

	private AIPlayerLoader aiPlayerLoader;

	public TutorialLoader() {
		aiPlayerLoader = new AIPlayerLoader();
	}

	public GameStateInitialization generateInitialGameState() {
		GameStateInitialization initialGameState = new GameStateInitialization();
		initialGameState.setGameUUID(UUID.randomUUID().toString());
		initialGameState.setPlayers(loadPlayers());
		initialGameState.setAncientPlanetResourceDegradationFactor(0.5f);;
		initialGameState.setGameTimeLimitMS(600_000);
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
		tutorialPlanetData.resourceGenerationSpeed = 1;
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
		tutorialShipShield.strength = 3f;
		tutorialShipShield.generation = 0.2f;
		tutorialShip.shields.add(tutorialShipShield);
		player.ships.add(tutorialShip);
		
		//------------------------------------------

		AiPlayer aiPlayer = aiPlayerLoader.initialize(1);
		aiPlayer.id = 2;
		aiPlayer.teamId = 2;
		aiPlayer.turrets = new ArrayList<>();
		aiPlayer.ships.get(0).weapons.get(0).damage = 1;
		aiPlayer.ships.get(0).hull.strength = 3;
		aiPlayer.ships.get(0).shields.get(0).strength = 2;
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
    	ancientPlanet.setPosition(new Vector2(0, 200));
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
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(setupGuideEvent);
		
		PauseGuideEvent initialDelayGuideEvent = new PauseGuideEvent();
		initialDelayGuideEvent.setDuration(1f);
		guideQueue.add(initialDelayGuideEvent);

		DialogGuideEvent firstMessageGuideEvent = new DialogGuideEvent();
		firstMessageGuideEvent.setDuration(1.5f);
		firstMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.CENTER;
			}
			
			@Override
			public String getText() {
				return "Attention!";
			}
			
		});
		firstMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.updateUserVoiceVolume(0.7f);
				AudioManager.playVoice("tutorial_voiceline_1");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(firstMessageGuideEvent);

		if (mode == TutorialMode.FULL) {
			DialogGuideEvent secondMessageGuideEvent = new DialogGuideEvent();
			secondMessageGuideEvent.setDuration(6.5f);
			secondMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.CENTER;
				}
				
				@Override
				public String getText() {
					return "The metrics from your brainwave analysis\nindicate a significant complexity\nin strategic computation";
				}
				
			});
			secondMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_2");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(secondMessageGuideEvent);

			DialogGuideEvent fourthMessageGuideEvent = new DialogGuideEvent();
			fourthMessageGuideEvent.setDuration(6f);
			fourthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.CENTER;
				}
				
				@Override
				public String getText() {
					return "By martial law of the terran aristrocracy,\n you have been assigned the career of\na commander";
				}
				
			});
			fourthMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_3");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(fourthMessageGuideEvent);
		}
		
		if (mode == TutorialMode.FULL) {
			DialogGuideEvent fifthMessageGuideEvent = new DialogGuideEvent();
			fifthMessageGuideEvent.setDuration(2.5f);
			fifthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.CENTER;
				}
				
				@Override
				public String getText() {
					return "I will guide you through the basics";
				}
				
			});
			fifthMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_4");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(fifthMessageGuideEvent);
			
			PauseGuideEvent anotherDelayGuideEvent = new PauseGuideEvent();
			anotherDelayGuideEvent.setDuration(1f);
			guideQueue.add(anotherDelayGuideEvent);
			
			DialogGuideEvent sateliteMessageGuideEvent = new DialogGuideEvent();
			sateliteMessageGuideEvent.setDuration(7f);
			sateliteMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.CENTER;
				}
				
				@Override
				public String getText() {
					return "We have updated your personal digital\ninterface with the communication software for\nour quantum tunnel projector satellite";
				}
				
			});
			sateliteMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_5");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(sateliteMessageGuideEvent);
			
			DialogGuideEvent fleetMessageGuideEvent = new DialogGuideEvent();
			fleetMessageGuideEvent.setDuration(6f);
			fleetMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.CENTER;
				}
				
				@Override
				public String getText() {
					return "giving you realtime access to commands\nand information of your fleet\nall across the universe";
				}
				
			});
			guideQueue.add(fleetMessageGuideEvent);
		}
		
		DialogGuideEvent sixthsMessageGuideEvent = new DialogGuideEvent();
		sixthsMessageGuideEvent.setDuration(3f);
		sixthsMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Initializing training combat simulation";
			}
			
		});
		sixthsMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_6");
			}
			
			@Override
			public void executeAtEnd() {
				GameModel.visible = true;
				AudioManager.playMusic("eqspace1", true);
				AudioManager.startRadio();
			}
			
		});
		guideQueue.add(sixthsMessageGuideEvent);
		
		PauseGuideEvent secondDelayGuideEvent = new PauseGuideEvent();
		secondDelayGuideEvent.setDuration(2f);
		guideQueue.add(secondDelayGuideEvent);
		
		DialogGuideEvent timeMessageGuideEvent = new DialogGuideEvent();
		timeMessageGuideEvent.setDuration(4f);
		timeMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "In the top right corner, you can see the\nremaining mission time";
			}
			
		});
		timeMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				Game.setUIElementVisible("time", true);
				AudioManager.playVoice("tutorial_voiceline_7");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(timeMessageGuideEvent);
		
		if (mode == TutorialMode.FULL) {
			DialogGuideEvent time2MessageGuideEvent = new DialogGuideEvent();
			time2MessageGuideEvent.setDuration(4f);
			time2MessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "When expired, an algorithm will\ndecide over your net performance";
				}
				
			});
			time2MessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					Game.setUIElementVisible("time", true);
					AudioManager.playVoice("tutorial_voiceline_8");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(time2MessageGuideEvent);
		}

		CameraMoveGuideEvent initialGuideEvent = new CameraMoveGuideEvent();
		initialGuideEvent.setDuration(2f);
		initialGuideEvent.setTargetCameraPos(new Vector3(0f, -800f, 2000f));
		guideQueue.add(initialGuideEvent);
		
		DialogGuideEvent seventhMessageGuideEvent = new DialogGuideEvent();
		seventhMessageGuideEvent.setDuration(3.5f);
		seventhMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "The blue planet is under your control";
			}
			
		});
		seventhMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_9");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(seventhMessageGuideEvent);
		
		DialogGuideEvent eigthMessageGuideEvent = new DialogGuideEvent();
		eigthMessageGuideEvent.setDuration(5f);
		eigthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "It generates resources, represented by\nthe blue circle around it";
			}
			
		});
		eigthMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_10");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(eigthMessageGuideEvent);
		
		PauseGuideEvent thirdDelayGuideEvent = new PauseGuideEvent();
		thirdDelayGuideEvent.setDuration(1f);
		guideQueue.add(thirdDelayGuideEvent);

		CameraMoveGuideEvent moveToNeutralGuideEvent = new CameraMoveGuideEvent();
		moveToNeutralGuideEvent.setDuration(2f);
		moveToNeutralGuideEvent.setTargetCameraPos(new Vector3(-50f, -700f, 2000f));
		guideQueue.add(moveToNeutralGuideEvent);

		if (mode == TutorialMode.FULL) {
			DialogGuideEvent tenthMessageGuideEvent = new DialogGuideEvent();
			tenthMessageGuideEvent.setDuration(3f);
			tenthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "The gray planet is uninhabited";
				}
				
			});
			tenthMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_11");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(tenthMessageGuideEvent);
		}

		DialogGuideEvent firstFleetMoveGuideEvent = new DialogGuideEvent();
		firstFleetMoveGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Drag from your planet to the neutral one";
			}
			
		});
		firstFleetMoveGuideEvent.setCondition(new ConditionalBehavior() {

			@Override
			public boolean isStartConditionMet() {
				return true;
			}

			@Override
			public boolean isEndConditionMet() {
				for (Ship ship : GameModel.activeGameState.ships) {
					if (ship.ownerId == 1) {
						return true;
					}
				}
				return false;
			}
			
		});
		firstFleetMoveGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				Game.setUIElementVisible("ownCP", true);
				GameModel.inputEnabled = true;
				AudioManager.playVoice("tutorial_voiceline_12");
			}
			
			@Override
			public void executeAtEnd() {
				GameModel.inputEnabled = false;
			}
			
		});
		guideQueue.add(firstFleetMoveGuideEvent);
		
		DialogGuideEvent ownShipSendPauseMessageGuideEvent = new DialogGuideEvent();
		ownShipSendPauseMessageGuideEvent.setDuration(1f);
		ownShipSendPauseMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				
			}
			
			@Override
			public void executeAtEnd() {
				Game.pause();
			}
			
		});
		guideQueue.add(ownShipSendPauseMessageGuideEvent);
		
		if (mode == TutorialMode.FULL) {
			DialogGuideEvent sentShipGuideEvent = new DialogGuideEvent();
			sentShipGuideEvent.setDuration(3.5f);
			sentShipGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "You have issued a command for a ship\nto travel to the neutral planet";
				}
				
			});
			sentShipGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_13");
				}
				
				@Override
				public void executeAtEnd() {
					
				}
				
			});
			guideQueue.add(sentShipGuideEvent);
		}
		
		DialogGuideEvent resourceCostMessageGuideEvent = new DialogGuideEvent();
		resourceCostMessageGuideEvent.setDuration(7f);
		resourceCostMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Every ship costs planetary resources,\nas well as a varying amount of\nCommand Points (CP)";
			}
			
		});
		resourceCostMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_14");
			}
			
			@Override
			public void executeAtEnd() {
				
			}
			
		});
		guideQueue.add(resourceCostMessageGuideEvent);

		if (mode == TutorialMode.FULL) {
			DialogGuideEvent thirteenthMessageGuideEvent = new DialogGuideEvent();
			thirteenthMessageGuideEvent.setDuration(3f);
			thirteenthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "You can see your CP meter\nat the bottom";
				}
				
			});
			thirteenthMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_15");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(thirteenthMessageGuideEvent);
		}

		DialogGuideEvent fifteenthMessageGuideEvent = new DialogGuideEvent();
		fifteenthMessageGuideEvent.setDuration(4f);
		fifteenthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Both CP and planetary resources\nregenerate gradually over time";
			}
			
		});
		fifteenthMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_16");
			}
			
			@Override
			public void executeAtEnd() {
				Game.unpause();
			}
			
		});
		guideQueue.add(fifteenthMessageGuideEvent);

		DialogGuideEvent firstFleetMoveGuideFinishEvent = new DialogGuideEvent();
		firstFleetMoveGuideFinishEvent.setCondition(new ConditionalBehavior() {
			
			@Override
			public boolean isStartConditionMet() {
				return true;
			}
			
			@Override
			public boolean isEndConditionMet() {
				for (Ship ship : GameModel.activeGameState.ships) {
					if (ship.ownerId == 1) {
						return false;
					}
				}
				return true;
			}
			
		});
		guideQueue.add(firstFleetMoveGuideFinishEvent);

		if (mode == TutorialMode.FULL) {
			DialogGuideEvent sixteenthMessageGuideEvent = new DialogGuideEvent();
			sixteenthMessageGuideEvent.setDuration(5f);
			sixteenthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "You gained control over the previously\nneutral planet and its resource production";
				}
				
			});
			sixteenthMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_17");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(sixteenthMessageGuideEvent);
		}

		DialogGuideEvent seventeenthMessageGuideEvent = new DialogGuideEvent();
		seventeenthMessageGuideEvent.setDuration(4.5f);
		seventeenthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "You can also send ships between\nyour own planets to transfer resources";
			}
			
		});
		seventeenthMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_18");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(seventeenthMessageGuideEvent);

		CameraMoveGuideEvent panAwayToEnemyGuideEvent = new CameraMoveGuideEvent();
		panAwayToEnemyGuideEvent.setDuration(2f);
		panAwayToEnemyGuideEvent.setTargetCameraPos(new Vector3(130f, -700f, 3000f));
		guideQueue.add(panAwayToEnemyGuideEvent);
		
		CameraMoveGuideEvent panCloseToEnemyGuideEvent = new CameraMoveGuideEvent();
		panCloseToEnemyGuideEvent.setDuration(2f);
		panCloseToEnemyGuideEvent.setTargetCameraPos(new Vector3(320f, -700f, 2300f));
		guideQueue.add(panCloseToEnemyGuideEvent);
		
		DialogGuideEvent twentysecondMessageGuideEvent = new DialogGuideEvent();
		twentysecondMessageGuideEvent.setDuration(4.5f);
		twentysecondMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "The red planet indicates, that it\nis controlled by 'the Turbulence'";
			}
			
		});
		twentysecondMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_19");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(twentysecondMessageGuideEvent);
		
		if (mode == TutorialMode.FULL) {
			DialogGuideEvent secondTurbulenceMessageGuideEvent = new DialogGuideEvent();
			secondTurbulenceMessageGuideEvent.setDuration(5.8f);
			secondTurbulenceMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "The Turbulence is a scarcely researched,\nhighly intelligent, silicon based, collective\nlife form";
				}
				
			});
			secondTurbulenceMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_20");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(secondTurbulenceMessageGuideEvent);
			
			DialogGuideEvent thirdTurbulenceMessageGuideEvent = new DialogGuideEvent();
			thirdTurbulenceMessageGuideEvent.setDuration(4f);
			thirdTurbulenceMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "Most of all, it is extremely hostile\ntowards other lifeforms";
				}
				
			});
			guideQueue.add(thirdTurbulenceMessageGuideEvent);
		}
		
		DialogGuideEvent enemyCPMessageGuideEvent = new DialogGuideEvent();
		enemyCPMessageGuideEvent.setDuration(7f);
		enemyCPMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Our scanners have been trained to\ncalculate the Turbulence collectives CP,\nas indicated by the red meter at the top left";
			}
			
		});
		enemyCPMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				Game.setUIElementVisible("enemy1CP", true);
				AudioManager.playVoice("tutorial_voiceline_21");
			}
			
			@Override
			public void executeAtEnd() {
				FleetMovementMessage aiFleetMoveMessage = new FleetMovementMessage();
				aiFleetMoveMessage.userUUID = "2";
				aiFleetMoveMessage.fromPlanetId = 1;
				aiFleetMoveMessage.toPlanetId = 3;
				GameModel.outputMessageQueue.add(aiFleetMoveMessage);
			}
			
		});
		guideQueue.add(enemyCPMessageGuideEvent);
		
		CameraMoveGuideEvent panAwayToMapGuideEvent = new CameraMoveGuideEvent();
		panAwayToMapGuideEvent.setDuration(3.5f);
		panAwayToMapGuideEvent.setTargetCameraPos(new Vector3(130f, -700f, 3000f));
		guideQueue.add(panAwayToMapGuideEvent);
		
		CameraMoveGuideEvent panToShipGuideEvent = new CameraMoveGuideEvent();
		panToShipGuideEvent.setDuration(2f);
		DialogGuideEvent twentyfourthMessageGuideEvent = new DialogGuideEvent();
		twentyfourthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Attack the now hostile planet";
			}
			
		});
		twentyfourthMessageGuideEvent.setCondition(new ConditionalBehavior() {
			
			@Override
			public boolean isStartConditionMet() {
				return true;
			}
			
			@Override
			public boolean isEndConditionMet() {
				for (Ship ship : GameModel.activeGameState.ships) {
					if (ship.ownerId == 1) {
						if (ship.targetPlanetId == 3 || ship.targetPlanetId == 1) {
							return true;
						}
					}
				}
				return false;
			}
			
		});
		twentyfourthMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				GameModel.inputEnabled = true;
				AudioManager.playVoice("tutorial_voiceline_22");
			}
			
			@Override
			public void executeAtEnd() {
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
			
		});
		guideQueue.add(twentyfourthMessageGuideEvent);
		
		DialogGuideEvent enemyShipSendPauseMessageGuideEvent = new DialogGuideEvent();
		enemyShipSendPauseMessageGuideEvent.setDuration(0.5f);
		guideQueue.add(enemyShipSendPauseMessageGuideEvent);
		
		DialogGuideEvent enemyShipSendMessageGuideEvent = new DialogGuideEvent();
		enemyShipSendMessageGuideEvent.setDuration(3.5f);
		enemyShipSendMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "The Turbulence has sent a ship\nto defend its planet";
			}
			
		});
		enemyShipSendMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_23");
				Game.pause();
			}
			
			@Override
			public void executeAtEnd() {
				
			}
			
		});
		guideQueue.add(enemyShipSendMessageGuideEvent);
		
		
		DialogGuideEvent enemyShipSend2MessageGuideEvent = new DialogGuideEvent();
		enemyShipSend2MessageGuideEvent.setDuration(4.5f);
		enemyShipSend2MessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "When in range, ships will automatically\nattack each other, regardless of\ntheir route";
			}
			
		});
		enemyShipSend2MessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_24");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(enemyShipSend2MessageGuideEvent);
		guideQueue.add(panToShipGuideEvent);
		
		DialogGuideEvent enemyShipSend3MessageGuideEvent = new DialogGuideEvent();
		enemyShipSend3MessageGuideEvent.setDuration(6f);
		enemyShipSend3MessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Below the ships you can see their blue\nshield bar and underneath it their\narmor in brown";
			}
			
		});
		enemyShipSend3MessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_25");
			}
			
			@Override
			public void executeAtEnd() {
				Game.unpause();
			}
			
		});
		guideQueue.add(enemyShipSend3MessageGuideEvent);
		
		CameraMoveGuideEvent panAgainAwayToMapGuideEvent = new CameraMoveGuideEvent();
		panAgainAwayToMapGuideEvent.setDuration(2f);
		panAgainAwayToMapGuideEvent.setTargetCameraPos(new Vector3(200f, -600f, 1800f));
		guideQueue.add(panAgainAwayToMapGuideEvent);
		
		DialogGuideEvent twentyfifthMessageGuideEvent = new DialogGuideEvent();
		twentyfifthMessageGuideEvent.setDuration(6f);
		twentyfifthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Eliminating all hostile planets and ships\nis one way to achieve definitive mission success";
			}
			
		});
		twentyfifthMessageGuideEvent.setCondition(new ConditionalBehavior() {
			
			@Override
			public boolean isStartConditionMet() {
				return EngineUtility.getPlanet(GameModel.activeGameState.planets, 3).ownerId == 1 || EngineUtility.getPlanet(GameModel.activeGameState.planets, 1).ownerId == 1;
			}
			
			@Override
			public boolean isEndConditionMet() {
				return twentyfifthMessageGuideEvent.getElapsed() > twentyfifthMessageGuideEvent.getDuration();
			}
		});
		twentyfifthMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_26");
			}
			
			@Override
			public void executeAtEnd() {
				Game.unpause();
			}
			
		});
		guideQueue.add(twentyfifthMessageGuideEvent);
		
		CameraMoveGuideEvent panToAncientCloseGuideEvent = new CameraMoveGuideEvent();
		panToAncientCloseGuideEvent.setDuration(2f);
		panToAncientCloseGuideEvent.setTargetCameraPos(new Vector3(0f, 0f, 2000f));
		guideQueue.add(panToAncientCloseGuideEvent);
		
		DialogGuideEvent ancientIntroMessageGuideEvent = new DialogGuideEvent();
		ancientIntroMessageGuideEvent.setDuration(5f);
		ancientIntroMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "The yellow planet is a star where unknown,\nancient technology has been detected";
			}
			
		});
		ancientIntroMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_27");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(ancientIntroMessageGuideEvent);
		
		CameraMoveGuideEvent panAwayToMapFromAncientGuideEvent = new CameraMoveGuideEvent();
		panAwayToMapFromAncientGuideEvent.setDuration(2f);
		panAwayToMapFromAncientGuideEvent.setTargetCameraPos(new Vector3(130f, -200f, 3800f));
		guideQueue.add(panAwayToMapFromAncientGuideEvent);
		
		if (mode == TutorialMode.FULL) {
			DialogGuideEvent ancientShipInfoMessageGuideEvent = new DialogGuideEvent();
			ancientShipInfoMessageGuideEvent.setDuration(9.5f);
			ancientShipInfoMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "We have equipped our fleet with a magnetic\nheat and radiation shield to withstand the\nextreme condition of stars to allow our\nships to extract this ancient technology";
				}
				
			});
			ancientShipInfoMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_29");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(ancientShipInfoMessageGuideEvent);
			
			DialogGuideEvent secondAncientDecreaseInfoMessageGuideEvent = new DialogGuideEvent();
			secondAncientDecreaseInfoMessageGuideEvent.setDuration(7.5f);
			secondAncientDecreaseInfoMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "Unfortunately, this procedure requires extreme\namounts of dark matter, which our ships\ncan only safely store so much of";
				}
				
			});
			secondAncientDecreaseInfoMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_30");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(secondAncientDecreaseInfoMessageGuideEvent);
		}
		
		DialogGuideEvent ancientDecreaseInfoMessageGuideEvent = new DialogGuideEvent();
		ancientDecreaseInfoMessageGuideEvent.setDuration(6f);
		ancientDecreaseInfoMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "When captured, your planetary resources\non the star decrease, not increase over time";
			}
			
		});
		ancientDecreaseInfoMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_31");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(ancientDecreaseInfoMessageGuideEvent);
		
		DialogGuideEvent ancientCaptureMessageGuideEvent = new DialogGuideEvent();
		ancientCaptureMessageGuideEvent.setDuration(3f);
		ancientCaptureMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Send a ship to the yellow planet";
			}
			
		});
		ancientCaptureMessageGuideEvent.setCondition(new ConditionalBehavior() {
			
			@Override
			public boolean isStartConditionMet() {
				return true;
			}
			
			@Override
			public boolean isEndConditionMet() {
				return EngineUtility.getPlanet(GameModel.activeGameState.planets, 2).ownerId == 1;
			}
		});
		ancientCaptureMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				GameModel.inputEnabled = true;
				AudioManager.playVoice("tutorial_voiceline_28");
			}
			
			@Override
			public void executeAtEnd() {
				GameModel.inputEnabled = false;
			}
			
		});
		guideQueue.add(ancientCaptureMessageGuideEvent);
		
		DialogGuideEvent ancientUIMessageGuideEvent = new DialogGuideEvent();
		ancientUIMessageGuideEvent.setDuration(7.5f);
		ancientUIMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "While under control, you generate\nAncient Technology Points (AP), as shown in\nthe yellow bar on the left side";
			}
			
		});
		ancientUIMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				Game.setUIElementVisible("teamAP", true);
				Game.setUIElementVisible("enemyAP", true);
				AudioManager.playVoice("tutorial_voiceline_32");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(ancientUIMessageGuideEvent);
		
		DialogGuideEvent ancientWinConditionInfoMessageGuideEvent = new DialogGuideEvent();
		ancientWinConditionInfoMessageGuideEvent.setDuration(7f);
		ancientWinConditionInfoMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "If your AP-bar is full, the mission is\nalso immediately considered successful,\nregardless of planetary control status";
			}
			
		});
		ancientWinConditionInfoMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_33");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(ancientWinConditionInfoMessageGuideEvent);
		
		if (mode == TutorialMode.FULL) {
			DialogGuideEvent ancientOwnLossInfoMessageGuideEvent = new DialogGuideEvent();
			ancientOwnLossInfoMessageGuideEvent.setDuration(4.5f);
			ancientOwnLossInfoMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "When the ancient planet resources are\nempty, you lose control over it";
				}
				
			});
			ancientOwnLossInfoMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					AudioManager.playVoice("tutorial_voiceline_34");
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(ancientOwnLossInfoMessageGuideEvent);
		}
		
		DialogGuideEvent timeoutConditionInfoMessageGuideEvent = new DialogGuideEvent();
		timeoutConditionInfoMessageGuideEvent.setDuration(7f);
		timeoutConditionInfoMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "If the time runs out, the faction with\nthe most planets under control is\nconsidered victorious over the system";
			}
			
		});
		timeoutConditionInfoMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				AudioManager.playVoice("tutorial_voiceline_35");
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(timeoutConditionInfoMessageGuideEvent);

		DialogGuideEvent finishGuideEvent = new DialogGuideEvent();
		finishGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Complete the mission in any way!";
			}
			
		});
		finishGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				GameModel.inputEnabled = true;
				GameModel.activeGameState.maxGameTimeMS = GameModel.activeGameState.gameTimeMS + 60_000;
				AudioManager.playVoice("tutorial_voiceline_36");
				AudioManager.updateUserVoiceVolume(0.5f);
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		finishGuideEvent.setCondition(new ConditionalBehavior() {
			
			@Override
			public boolean isStartConditionMet() {
				return true;
			}
			
			@Override
			public boolean isEndConditionMet() {
				return GameModel.activeGameState.winner == 1;
			}
			
		});
		guideQueue.add(finishGuideEvent);

		return guideQueue;
	}

}
