package de.instinct.eqfleet.game.backend.driver.local.tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.ai.AiDifficulty;
import de.instinct.engine.ai.AiEngine;
import de.instinct.engine.initialization.GameStateInitialization;
import de.instinct.engine.map.GameMap;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.ShipType;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.behavior.ActionBehavior;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.behavior.ConditionalBehavior;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.behavior.MessageBehavior;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.CameraMoveGuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.DialogGuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.PauseGuideEvent;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.VerticalAlignment;

public class TutorialLoader {

	private AiEngine aiEngine;

	public TutorialLoader() {
		aiEngine = new AiEngine();
	}

	public GameStateInitialization generateInitialGameState() {
		GameStateInitialization initialGameState = new GameStateInitialization();
		initialGameState.gameUUID = UUID.randomUUID().toString();
		initialGameState.players = loadPlayers();
		initialGameState.map = new GameMap();
		initialGameState.map.planets = new ArrayList<>();
		initialGameState.atpToWin = 30;
		return initialGameState;
	}

	private List<Player> loadPlayers() {
		List<Player> players = new ArrayList<>();
		
		Player neutralPlayer = new Player();
		neutralPlayer.id = 0;
		neutralPlayer.teamId = 0;
		players.add(neutralPlayer);
		
		Player player1 = new Player();
		player1.id = 1;
		player1.teamId = 1;
		player1.name = "Player 1";
		player1.ships = new ArrayList<>();
		ShipData tutorialShip = new ShipData();
		tutorialShip.type = ShipType.FIGHTER;
		tutorialShip.model = "hawk";
		tutorialShip.movementSpeed = 100f;
		tutorialShip.cost = 3;
		player1.ships.add(tutorialShip);
		player1.maxCommandPoints = 10;
		player1.startCommandPoints = 1;
		player1.commandPointsGenerationSpeed = 0.2;
		player1.currentCommandPoints = player1.startCommandPoints;
		players.add(player1);

		AiPlayer aiPlayer = aiEngine.initialize(AiDifficulty.RETARDED);
		aiPlayer.id = 2;
		aiPlayer.teamId = 2;
		aiPlayer.ships = new ArrayList<>();
		ShipData aiTutorialShip = new ShipData();
		aiTutorialShip.type = ShipType.FIGHTER;
		aiTutorialShip.model = "hawk";
		aiTutorialShip.movementSpeed = 100f;
		aiTutorialShip.cost = 3;
		aiPlayer.ships.add(aiTutorialShip);
		aiPlayer.currentCommandPoints = aiPlayer.startCommandPoints;
		players.add(aiPlayer);

		return players;
	}

	private List<Planet> generateMap() {
		List<Planet> planets = new ArrayList<>();

		/*Planet startPlanetPlayerOne = new Planet();
		startPlanetPlayerOne.id = 0;
		startPlanetPlayerOne.ownerId = 1;
		startPlanetPlayerOne.value = 15;
		startPlanetPlayerOne.xPos = 0;
		startPlanetPlayerOne.yPos = -(EngineUtility.MAP_BOUNDS.y / 2) + EngineUtility.PLANET_RADIUS + 150;
		planets.add(startPlanetPlayerOne);

		Planet startPlanetPlayerTwo = new Planet();
		startPlanetPlayerTwo.id = 1;
		startPlanetPlayerTwo.ownerId = 2;
		startPlanetPlayerTwo.value = 20;
		startPlanetPlayerTwo.xPos = 350;
		startPlanetPlayerTwo.yPos = -(EngineUtility.MAP_BOUNDS.y / 2) + EngineUtility.PLANET_RADIUS + 150;
		planets.add(startPlanetPlayerTwo);

		Planet ancientPlanet = new Planet();
		ancientPlanet.id = 2;
		ancientPlanet.ownerId = 0;
		ancientPlanet.value = 0;
		ancientPlanet.xPos = 0;
		ancientPlanet.yPos = 200;
		ancientPlanet.ancient = true;
		planets.add(ancientPlanet);

		Planet neutralPlanet6 = new Planet();
		neutralPlanet6.id = 7;
		neutralPlanet6.ownerId = 0;
		neutralPlanet6.value = 10;
		neutralPlanet6.xPos = 400;
		neutralPlanet6.yPos = -400;
		planets.add(neutralPlanet6);

		Planet neutralPlanet7 = new Planet();
		neutralPlanet7.id = 8;
		neutralPlanet7.ownerId = 0;
		neutralPlanet7.value = 10;
		neutralPlanet7.xPos = -150;
		neutralPlanet7.yPos = -500;
		planets.add(neutralPlanet7);*/

		return planets;
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
		firstMessageGuideEvent.setDuration(2f);
		firstMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.CENTER;
			}
			
			@Override
			public String getText() {
				return "Attention, Cadet!";
			}
			
		});
		guideQueue.add(firstMessageGuideEvent);

		if (mode == TutorialMode.STORY_FULL) {
			DialogGuideEvent secondMessageGuideEvent = new DialogGuideEvent();
			secondMessageGuideEvent.setDuration(4f);
			secondMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.CENTER;
				}
				
				@Override
				public String getText() {
					return "The talent algorithm has\nevaluated your data.";
				}
				
			});
			guideQueue.add(secondMessageGuideEvent);

			DialogGuideEvent thirdMessageGuideEvent = new DialogGuideEvent();
			thirdMessageGuideEvent.setDuration(4f);
			thirdMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.CENTER;
				}
				
				@Override
				public String getText() {
					return "You score highest on\n'Strategic Computation'.";
				}
				
			});
			guideQueue.add(thirdMessageGuideEvent);

			DialogGuideEvent fourthMessageGuideEvent = new DialogGuideEvent();
			fourthMessageGuideEvent.setDuration(4f);
			fourthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.CENTER;
				}
				
				@Override
				public String getText() {
					return "Therefor you have been assigned\nthe career of a Commander.";
				}
				
			});
			guideQueue.add(fourthMessageGuideEvent);
		}
		
		DialogGuideEvent fifthMessageGuideEvent = new DialogGuideEvent();
		fifthMessageGuideEvent.setDuration(3f);
		fifthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "I'll guide you through the basics.";
			}
			
		});
		fifthMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				
			}
			
			@Override
			public void executeAtEnd() {
				
			}
		});
		guideQueue.add(fifthMessageGuideEvent);
		
		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent sixthsMessageGuideEvent = new DialogGuideEvent();
			sixthsMessageGuideEvent.setDuration(3f);
			sixthsMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "This is a simple combat simulation.";
				}
				
			});
			guideQueue.add(sixthsMessageGuideEvent);
		}
		
		DialogGuideEvent timeMessageGuideEvent = new DialogGuideEvent();
		timeMessageGuideEvent.setDuration(5f);
		timeMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "In the top right corner you can\nsee the remaining time.";
			}
			
		});
		timeMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				Game.setUIElementVisible("time", true);
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(timeMessageGuideEvent);


		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			CameraMoveGuideEvent initialGuideEvent = new CameraMoveGuideEvent();
			initialGuideEvent.setDuration(2f);
			initialGuideEvent.setTargetCameraPos(new Vector3(0f, -800f, 2000f));
			guideQueue.add(initialGuideEvent);
			
			DialogGuideEvent seventhMessageGuideEvent = new DialogGuideEvent();
			seventhMessageGuideEvent.setDuration(3f);
			seventhMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "The green planet is yours.";
				}
				
			});
			guideQueue.add(seventhMessageGuideEvent);
			
			DialogGuideEvent eigthMessageGuideEvent = new DialogGuideEvent();
			eigthMessageGuideEvent.setDuration(4f);
			eigthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "It generates military resources,\nas quantified by the number.";
				}
				
			});
			guideQueue.add(eigthMessageGuideEvent);
		}

		CameraMoveGuideEvent moveToNeutralGuideEvent = new CameraMoveGuideEvent();
		moveToNeutralGuideEvent.setDuration(2f);
		moveToNeutralGuideEvent.setTargetCameraPos(new Vector3(-50f, -700f, 2000f));
		guideQueue.add(moveToNeutralGuideEvent);

		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent tenthMessageGuideEvent = new DialogGuideEvent();
			tenthMessageGuideEvent.setDuration(3f);
			tenthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "The gray planet is uninhabited.";
				}
				
			});
			guideQueue.add(tenthMessageGuideEvent);

			DialogGuideEvent eleventhMessageGuideEvent = new DialogGuideEvent();
			eleventhMessageGuideEvent.setDuration(3f);
			eleventhMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "The number on it shows the\nresources needed to claim it.";
				}
				
			});
			guideQueue.add(eleventhMessageGuideEvent);
		}

		DialogGuideEvent firstFleetMoveGuideEvent = new DialogGuideEvent();
		firstFleetMoveGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Drag from your planet\nto the neutral one.";
			}
			
		});
		/*firstFleetMoveGuideEvent.setCondition(new ConditionalBehavior() {
			
			private FleetMovementEvent initiatedEvent;

			@Override
			public boolean isStartConditionMet() {
				return true;
			}

			@Override
			public boolean isEndConditionMet() {
				GameEvent event = GameModel.activeGameState.activeEvents.peek();
				if (initiatedEvent == null) {
					if (event != null) {
						if (event instanceof FleetMovementEvent) {
							if (((FleetMovementEvent) event).playerId == 1) {
								return true;
							}
						}
					}
				}
				return false;
			}
			
		});*/
		firstFleetMoveGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				Game.setUIElementVisible("ownCP", true);
				GameModel.inputEnabled = true;
			}
			
			@Override
			public void executeAtEnd() {
				Game.pause();
				GameModel.inputEnabled = false;
			}
			
		});
		guideQueue.add(firstFleetMoveGuideEvent);
		
		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent addedMidMessageGuideEvent = new DialogGuideEvent();
			addedMidMessageGuideEvent.setDuration(5f);
			addedMidMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "You have now sent\nships to the neutral planet.";
				}
				
			});
			guideQueue.add(addedMidMessageGuideEvent);
		}

		DialogGuideEvent thirteenthMessageGuideEvent = new DialogGuideEvent();
		thirteenthMessageGuideEvent.setDuration(5f);
		thirteenthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "The bar below represents\nyour Command Points (CP).";
			}
			
		});
		guideQueue.add(thirteenthMessageGuideEvent);

		DialogGuideEvent fourteenthMessageGuideEvent = new DialogGuideEvent();
		fourteenthMessageGuideEvent.setDuration(3f);
		fourteenthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Every action consumes a CP.";
			}
			
		});
		guideQueue.add(fourteenthMessageGuideEvent);

		DialogGuideEvent fifteenthMessageGuideEvent = new DialogGuideEvent();
		fifteenthMessageGuideEvent.setDuration(3f);
		fifteenthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "CP regenerates passively over time.";
			}
			
		});
		fifteenthMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {}
			
			@Override
			public void executeAtEnd() {
				Game.unpause();
			}
			
		});
		guideQueue.add(fifteenthMessageGuideEvent);

		DialogGuideEvent firstFleetMoveGuideFinishEvent = new DialogGuideEvent();
		/*firstFleetMoveGuideFinishEvent.setCondition(new ConditionalBehavior() {
			
			private FleetMovementEvent initiatedEvent;
			
			@Override
			public boolean isStartConditionMet() {
				return true;
			}
			
			@Override
			public boolean isEndConditionMet() {
				GameEvent event = GameModel.activeGameState.activeEvents.peek();
				if (initiatedEvent == null) {
					if (event != null) {
						if (event instanceof FleetMovementEvent) {
							if (((FleetMovementEvent) event).playerId == 1) {
								initiatedEvent = (FleetMovementEvent) event;
							}
						}
					}
				} else {
					if (event == null) {
						return true;
					}
				}
				return false;
			}
			
		});*/
		guideQueue.add(firstFleetMoveGuideFinishEvent);

		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent sixteenthMessageGuideEvent = new DialogGuideEvent();
			sixteenthMessageGuideEvent.setDuration(3f);
			sixteenthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "You gained control of\nthe previously neutral planet.";
				}
				
			});
			guideQueue.add(sixteenthMessageGuideEvent);

			DialogGuideEvent seventeenthMessageGuideEvent = new DialogGuideEvent();
			seventeenthMessageGuideEvent.setDuration(4f);
			seventeenthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "It now produces resources for you.";
				}
				
			});
			guideQueue.add(seventeenthMessageGuideEvent);
		}

		DialogGuideEvent eighteenthMessageGuideEvent = new DialogGuideEvent();
		eighteenthMessageGuideEvent.setDuration(4f);
		eighteenthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "You can also send ships\nbetween your planets.";
			}
			
		});
		guideQueue.add(eighteenthMessageGuideEvent);

		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent secondFleetMoveGuideStartEvent = new DialogGuideEvent();
			secondFleetMoveGuideStartEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "Drag between your planets.";
				}
				
			});
			/*secondFleetMoveGuideStartEvent.setCondition(new ConditionalBehavior() {
				
				private FleetMovementEvent initiatedEvent;
				
				@Override
				public boolean isStartConditionMet() {
					return true;
				}
				
				@Override
				public boolean isEndConditionMet() {
					GameEvent event = GameModel.activeGameState.activeEvents.peek();
					if (initiatedEvent == null) {
						if (event != null) {
							if (event instanceof FleetMovementEvent) {
								if (((FleetMovementEvent) event).playerId == 1) {
									return true;
								}
							}
						}
					}
					return false;
				}
				
			});*/
			secondFleetMoveGuideStartEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					GameModel.inputEnabled = true;
				}
				
				@Override
				public void executeAtEnd() {
					Game.pause();
					GameModel.inputEnabled = false;
				}
				
			});
			guideQueue.add(secondFleetMoveGuideStartEvent);
			
			DialogGuideEvent twenteethMessageGuideEvent = new DialogGuideEvent();
			twenteethMessageGuideEvent.setDuration(6f);
			twenteethMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "When sent out, the\nresources between the fleet\nand planet are split in half.";
				}
				
			});
			guideQueue.add(twenteethMessageGuideEvent);
			
			DialogGuideEvent twentyfirstMessageGuideEvent = new DialogGuideEvent();
			twentyfirstMessageGuideEvent.setDuration(5f);
			twentyfirstMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "Rounded down, so a planet\nwith 7 resources creates 3 Ships.";
				}
				
			});
			guideQueue.add(twentyfirstMessageGuideEvent);
			
			DialogGuideEvent secondFleetMoveGuideFinishEvent = new DialogGuideEvent();
			/*secondFleetMoveGuideFinishEvent.setCondition(new ConditionalBehavior() {
				
				private FleetMovementEvent initiatedEvent;
				
				@Override
				public boolean isStartConditionMet() {
					return true;
				}
				
				@Override
				public boolean isEndConditionMet() {
					GameEvent event = GameModel.activeGameState.activeEvents.peek();
					if (initiatedEvent == null) {
						if (event != null) {
							if (event instanceof FleetMovementEvent) {
								if (((FleetMovementEvent) event).playerId == 1) {
									initiatedEvent = (FleetMovementEvent) event;
								}
							}
						}
					} else {
						if (event == null) {
							return true;
						}
					}
					return false;
				}
				
			});*/
			secondFleetMoveGuideFinishEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					Game.unpause();
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(secondFleetMoveGuideFinishEvent);
		}

		CameraMoveGuideEvent panAwayToEnemyGuideEvent = new CameraMoveGuideEvent();
		panAwayToEnemyGuideEvent.setDuration(2f);
		panAwayToEnemyGuideEvent.setTargetCameraPos(new Vector3(130f, -700f, 3000f));
		guideQueue.add(panAwayToEnemyGuideEvent);
		
		CameraMoveGuideEvent panCloseToEnemyGuideEvent = new CameraMoveGuideEvent();
		panCloseToEnemyGuideEvent.setDuration(2f);
		panCloseToEnemyGuideEvent.setTargetCameraPos(new Vector3(320f, -700f, 2300f));
		guideQueue.add(panCloseToEnemyGuideEvent);
		
		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent twentysecondMessageGuideEvent = new DialogGuideEvent();
			twentysecondMessageGuideEvent.setDuration(3f);
			twentysecondMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "Hostile planets are red.";
				}
				
			});
			
			twentysecondMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					
				}
				
				@Override
				public void executeAtEnd() {}
				
			});
			guideQueue.add(twentysecondMessageGuideEvent);
		}
		
		DialogGuideEvent enemyCPMessageGuideEvent = new DialogGuideEvent();
		enemyCPMessageGuideEvent.setDuration(3f);
		enemyCPMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Your opponent's CP are\nshown in the top left corner.";
			}
			
		});
		enemyCPMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				Game.setUIElementVisible("enemyCP", true);
			}
			
			@Override
			public void executeAtEnd() {
				FleetMovementMessage aiFleetMoveMessage = new FleetMovementMessage();
				aiFleetMoveMessage.userUUID = "2";
				aiFleetMoveMessage.fromPlanetId = 1;
				aiFleetMoveMessage.toPlanetId = 7;
				GameModel.outputMessageQueue.add(aiFleetMoveMessage);
			}
			
		});
		guideQueue.add(enemyCPMessageGuideEvent);
		
		DialogGuideEvent twentythirdMessageGuideEvent = new DialogGuideEvent();
		twentythirdMessageGuideEvent.setDuration(4f);
		twentythirdMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Reducing another planet's resources\nto 0 is not enough to capture it.";
			}
			
		});
		/*twentythirdMessageGuideEvent.setCondition(new ConditionalBehavior() {
			
			private FleetMovementEvent initiatedEvent;
			
			@Override
			public boolean isStartConditionMet() {
				GameEvent event = GameModel.activeGameState.activeEvents.peek();
				if (initiatedEvent == null) {
					if (event != null) {
						if (event instanceof FleetMovementEvent) {
							if (((FleetMovementEvent) event).playerId == 2) {
								initiatedEvent = (FleetMovementEvent) event;
							}
						}
					}
				} else {
					if (event == null) {
						return true;
					}
				}
				return false;
			}
			
			@Override
			public boolean isEndConditionMet() {
				return twentythirdMessageGuideEvent.getElapsed() > twentythirdMessageGuideEvent.getDuration();
			}
			
		});*/
		guideQueue.add(twentythirdMessageGuideEvent);
		
		CameraMoveGuideEvent panAwayToMapGuideEvent = new CameraMoveGuideEvent();
		panAwayToMapGuideEvent.setDuration(2f);
		panAwayToMapGuideEvent.setTargetCameraPos(new Vector3(130f, -700f, 3000f));
		guideQueue.add(panAwayToMapGuideEvent);
		
		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent twentyfourthMessageGuideEvent = new DialogGuideEvent();
			twentyfourthMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "Steal control of\nthe neutral planet.";
				}
				
			});
			twentyfourthMessageGuideEvent.setCondition(new ConditionalBehavior() {
				
				@Override
				public boolean isStartConditionMet() {
					return true;
				}
				
				@Override
				public boolean isEndConditionMet() {
					return EngineUtility.getPlanet(GameModel.activeGameState.planets, 7).ownerId == 1;
				}
				
			});
			twentyfourthMessageGuideEvent.setAction(new ActionBehavior() {
				
				@Override
				public void executeAtStart() {
					GameModel.inputEnabled = true;
				}
				
				@Override
				public void executeAtEnd() {
					GameModel.inputEnabled = false;
				}
				
			});
			guideQueue.add(twentyfourthMessageGuideEvent);
		}
		
		DialogGuideEvent twentyfifthMessageGuideEvent = new DialogGuideEvent();
		twentyfifthMessageGuideEvent.setDuration(3f);
		twentyfifthMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Eliminating all hostile planets\nand ships is one way to\nachieve Victory.";
			}
			
		});
		guideQueue.add(twentyfifthMessageGuideEvent);
		
		CameraMoveGuideEvent panToAncientCloseGuideEvent = new CameraMoveGuideEvent();
		panToAncientCloseGuideEvent.setDuration(2f);
		panToAncientCloseGuideEvent.setTargetCameraPos(new Vector3(0f, 0f, 2000f));
		guideQueue.add(panToAncientCloseGuideEvent);
		
		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent ancientIntroMessageGuideEvent = new DialogGuideEvent();
			ancientIntroMessageGuideEvent.setDuration(3f);
			ancientIntroMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "This is a planet where\nunknown, ancient technology\nhas been detected.";
				}
				
			});
			guideQueue.add(ancientIntroMessageGuideEvent);
		}
		
		
		CameraMoveGuideEvent panAwayToMapFromAncientGuideEvent = new CameraMoveGuideEvent();
		panAwayToMapFromAncientGuideEvent.setDuration(2f);
		panAwayToMapFromAncientGuideEvent.setTargetCameraPos(new Vector3(130f, -200f, 3200f));
		guideQueue.add(panAwayToMapFromAncientGuideEvent);
		
		DialogGuideEvent ancientCaptureMessageGuideEvent = new DialogGuideEvent();
		ancientCaptureMessageGuideEvent.setDuration(3f);
		ancientCaptureMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Send some ships to\nthe ancient (yellow) planet.";
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
			}
			
			@Override
			public void executeAtEnd() {
				GameModel.inputEnabled = false;
			}
			
		});
		guideQueue.add(ancientCaptureMessageGuideEvent);
		
		DialogGuideEvent ancientDecreaseInfoMessageGuideEvent = new DialogGuideEvent();
		ancientDecreaseInfoMessageGuideEvent.setDuration(4f);
		ancientDecreaseInfoMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "When captured,\nyour resources decrease,\nnot increase over time.";
			}
			
		});
		guideQueue.add(ancientDecreaseInfoMessageGuideEvent);
		
		DialogGuideEvent ancientUIMessageGuideEvent = new DialogGuideEvent();
		ancientUIMessageGuideEvent.setDuration(4f);
		ancientUIMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "While under control, you generate\nAncient Technology Points (AP),\nas shown on the left side.";
			}
			
		});
		ancientUIMessageGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				Game.setUIElementVisible("teamAP", true);
				Game.setUIElementVisible("enemyAP", true);
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		guideQueue.add(ancientUIMessageGuideEvent);
		
		DialogGuideEvent ancientWinConditionInfoMessageGuideEvent = new DialogGuideEvent();
		ancientWinConditionInfoMessageGuideEvent.setDuration(5f);
		ancientWinConditionInfoMessageGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "If your AP-bar is full,\nyou win the game.";
			}
			
		});
		guideQueue.add(ancientWinConditionInfoMessageGuideEvent);
		
		if (mode == TutorialMode.STORY_FULL || mode == TutorialMode.FULL) {
			DialogGuideEvent ancientOwnLossInfoMessageGuideEvent = new DialogGuideEvent();
			ancientOwnLossInfoMessageGuideEvent.setDuration(4f);
			ancientOwnLossInfoMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "When the ancient planet reaches 0,\nthe previous owner loses control.";
				}
				
			});
			guideQueue.add(ancientOwnLossInfoMessageGuideEvent);
			
			DialogGuideEvent timeoutConditionInfoMessageGuideEvent = new DialogGuideEvent();
			timeoutConditionInfoMessageGuideEvent.setDuration(4f);
			timeoutConditionInfoMessageGuideEvent.setMessage(new MessageBehavior() {
				
				@Override
				public VerticalAlignment getVerticalAlignment() {
					return VerticalAlignment.TOP;
				}
				
				@Override
				public String getText() {
					return "If the time runs out,\nthe team with the most AP wins.";
				}
				
			});
			guideQueue.add(timeoutConditionInfoMessageGuideEvent);
		}

		DialogGuideEvent finishGuideEvent = new DialogGuideEvent();
		finishGuideEvent.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return VerticalAlignment.TOP;
			}
			
			@Override
			public String getText() {
				return "Win the game in any way!";
			}
			
		});
		finishGuideEvent.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				GameModel.inputEnabled = true;
				GameModel.activeGameState.maxGameTimeMS = GameModel.activeGameState.gameTimeMS + 60_000;
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
				return false;
			}
			
		});
		guideQueue.add(finishGuideEvent);

		return guideQueue;
	}

}
