package de.instinct.eqfleet.game.frontend.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.ui.model.GameUIElement;
import de.instinct.eqfleet.game.frontend.ui.model.PlayerData;
import de.instinct.eqfleet.game.frontend.ui.model.UIBounds;
import de.instinct.eqfleet.menu.module.play.PlayModel;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class GameUILoader {
	
	private List<GameUIElement<?>> elements;
	
	private final String tagPrefix = "game_ui_";
	
	private final float AP_GLOWUP_DELAY = 2.8f;
	
	public List<GameUIElement<?>> loadElements() {
		elements = new ArrayList<>();
		UIBounds bounds = GameModel.uiBounds;
		loadTimeLabel(bounds);
		loadOwnCPBar(bounds);
		loadEnemy1CPBar(bounds);
		if (PlayModel.lobbyStatus != null && PlayModel.lobbyStatus.getType() != null && PlayModel.lobbyStatus.getType().getFactionMode().teamPlayerCount >= 2) {
			loadTeammate1CPBar(bounds);
			loadEnemy2CPBar(bounds);
		}
		if (PlayModel.lobbyStatus != null && PlayModel.lobbyStatus.getType() != null && PlayModel.lobbyStatus.getType().getFactionMode().teamPlayerCount >= 3) {
			loadTeammate2CPBar(bounds);
			loadEnemy3CPBar(bounds);
		}
		if (EngineUtility.mapHasAncient(GameModel.activeGameState)) {
			loadTeamAPBar(bounds);
			loadEnemyAPBar(bounds);
		}
		
		return elements;
	}

	private void loadTimeLabel(UIBounds bounds) {
		GameUIElement<BoxedRectangularLoadingBar> timeElement = GameUIElement.<BoxedRectangularLoadingBar>builder()
				.tag("time")
				.visible(true)
				.build();
		timeElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				
			}
			
		});
		timeElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				timeElement.setBounds(bounds.getTime());
			}
			
		});
		timeElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				long remainingMS = timeElement.getCurrentGameState().maxGameTimeMS - timeElement.getCurrentGameState().gameTimeMS;
				String remainingTimeLabel = StringUtils.generateCountdownLabel(remainingMS, false);
				Label timeLabel = new Label(remainingTimeLabel);
				timeLabel.setColor(remainingMS < 60_000 ? Color.RED : Color.WHITE);
				timeLabel.setBounds(bounds.getTime());
				timeLabel.render();
				drawRectangle(bounds.getTime(), Color.GRAY);
			}
			
		});
		elements.add(timeElement);
	}

	private void loadOwnCPBar(UIBounds bounds) {
		GameUIElement<BoxedRectangularLoadingBar> ownCPElement = GameUIElement.<BoxedRectangularLoadingBar>builder()
				.tag("ownCP")
				.visible(true)
				.element(createBar())
				.build();
		ownCPElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				
			}
			
		});
		ownCPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(ownCPElement.getCurrentGameState());
				ownCPElement.setBounds(bounds.getOwnCPBar());
				ownCPElement.getElement().setSegments((int)playerData.getSelf().maxCommandPoints);
				ownCPElement.getElement().setMaxValue(playerData.getSelf().maxCommandPoints);
				ownCPElement.getElement().setCurrentValue(playerData.getSelf().currentCommandPoints);
			}
			
		});
		ownCPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(ownCPElement.getCurrentGameState());
				Label cpLabel = new Label("CP");
				cpLabel.setColor(GameConfig.getPlayerColor(playerData.getSelf().id));
				cpLabel.setBounds(bounds.getOwnCPBarLabel());
				cpLabel.render();
				drawRectangle(bounds.getOwnCPBar(), GameConfig.getPlayerColor(playerData.getSelf().id));
				drawRectangle(bounds.getOwnCPBarLabel(), GameConfig.getPlayerColor(playerData.getSelf().id));
			}
			
		});
		elements.add(ownCPElement);
	}
	
	private void loadTeammate1CPBar(UIBounds bounds) {
		GameUIElement<BoxedRectangularLoadingBar> teammate1CPElement = GameUIElement.<BoxedRectangularLoadingBar>builder()
				.tag("teammate1CP")
				.visible(true)
				.element(createBar())
				.build();
		teammate1CPElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				
			}
			
		});
		teammate1CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teammate1CPElement.getCurrentGameState());
				teammate1CPElement.setBounds(bounds.getTeammate1CPBar());
				teammate1CPElement.getElement().setSegments((int)playerData.getTeammate1().maxCommandPoints);
				teammate1CPElement.getElement().setMaxValue(playerData.getTeammate1().maxCommandPoints);
				teammate1CPElement.getElement().setCurrentValue(playerData.getTeammate1().currentCommandPoints);
			}
			
		});
		teammate1CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teammate1CPElement.getCurrentGameState());
				Label cpLabel = new Label("CP");
				cpLabel.setColor(GameConfig.getPlayerColor(playerData.getTeammate1().id));
				cpLabel.setBounds(bounds.getTeammate1CPBarLabel());
				cpLabel.render();
				drawRectangle(bounds.getTeammate1CPBar(), GameConfig.getPlayerColor(playerData.getTeammate1().id));
				drawRectangle(bounds.getTeammate1CPBarLabel(), GameConfig.getPlayerColor(playerData.getTeammate1().id));
			}
			
		});
		elements.add(teammate1CPElement);
	}
	
	private void loadTeammate2CPBar(UIBounds bounds) {
		GameUIElement<BoxedRectangularLoadingBar> teammate2CPElement = GameUIElement.<BoxedRectangularLoadingBar>builder()
				.tag("teammate2CP")
				.visible(true)
				.element(createBar())
				.build();
		teammate2CPElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				
			}
			
		});
		teammate2CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teammate2CPElement.getCurrentGameState());
				teammate2CPElement.setBounds(bounds.getTeammate2CPBar());
				teammate2CPElement.getElement().setSegments((int)playerData.getTeammate2().maxCommandPoints);
				teammate2CPElement.getElement().setMaxValue(playerData.getTeammate2().maxCommandPoints);
				teammate2CPElement.getElement().setCurrentValue(playerData.getTeammate2().currentCommandPoints);
			}
			
		});
		teammate2CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teammate2CPElement.getCurrentGameState());
				Label cpLabel = new Label("CP");
				cpLabel.setColor(GameConfig.getPlayerColor(playerData.getTeammate2().id));
				cpLabel.setBounds(bounds.getTeammate2CPBarLabel());
				cpLabel.render();
				drawRectangle(bounds.getTeammate2CPBar(), GameConfig.getPlayerColor(playerData.getTeammate2().id));
				drawRectangle(bounds.getTeammate2CPBarLabel(), GameConfig.getPlayerColor(playerData.getTeammate2().id));
			}
			
		});
		elements.add(teammate2CPElement);
	}
	
	private void loadEnemy1CPBar(UIBounds bounds) {
		GameUIElement<BoxedRectangularLoadingBar> enemy1CPElement = GameUIElement.<BoxedRectangularLoadingBar>builder()
				.tag("enemy1CP")
				.visible(true)
				.element(createBar())
				.build();
		enemy1CPElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				
			}
			
		});
		enemy1CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy1CPElement.getCurrentGameState());
				enemy1CPElement.setBounds(bounds.getEnemy1CPBar());
				enemy1CPElement.getElement().setSegments((int)playerData.getEnemy1().maxCommandPoints);
				enemy1CPElement.getElement().setMaxValue(playerData.getEnemy1().maxCommandPoints);
				enemy1CPElement.getElement().setCurrentValue(playerData.getEnemy1().currentCommandPoints);
			}
			
		});
		enemy1CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy1CPElement.getCurrentGameState());
				Label cpLabel = new Label("CP");
				cpLabel.setColor(GameConfig.getPlayerColor(playerData.getEnemy1().id));
				cpLabel.setBounds(bounds.getEnemy1CPBarLabel());
				cpLabel.render();
				drawRectangle(bounds.getEnemy1CPBar(), GameConfig.getPlayerColor(playerData.getEnemy1().id));
				drawRectangle(bounds.getEnemy1CPBarLabel(), GameConfig.getPlayerColor(playerData.getEnemy1().id));
			}
			
		});
		elements.add(enemy1CPElement);
	}
	
	private void loadEnemy2CPBar(UIBounds bounds) {
		GameUIElement<BoxedRectangularLoadingBar> enemy2CPElement = GameUIElement.<BoxedRectangularLoadingBar>builder()
				.tag("enemy2CP")
				.visible(true)
				.element(createBar())
				.build();
		enemy2CPElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				
			}
			
		});
		enemy2CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy2CPElement.getCurrentGameState());
				enemy2CPElement.setBounds(bounds.getEnemy2CPBar());
				enemy2CPElement.getElement().setSegments((int)playerData.getEnemy2().maxCommandPoints);
				enemy2CPElement.getElement().setMaxValue(playerData.getEnemy2().maxCommandPoints);
				enemy2CPElement.getElement().setCurrentValue(playerData.getEnemy2().currentCommandPoints);
			}
			
		});
		enemy2CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy2CPElement.getCurrentGameState());
				drawRectangle(bounds.getEnemy2CPBar(), GameConfig.getPlayerColor(playerData.getEnemy2().id));
			}
			
		});
		elements.add(enemy2CPElement);
	}
	
	private void loadEnemy3CPBar(UIBounds bounds) {
		GameUIElement<BoxedRectangularLoadingBar> enemy3CPElement = GameUIElement.<BoxedRectangularLoadingBar>builder()
				.tag("enemy3CP")
				.visible(true)
				.element(createBar())
				.build();
		enemy3CPElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				
			}
			
		});
		enemy3CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy3CPElement.getCurrentGameState());
				enemy3CPElement.setBounds(bounds.getEnemy3CPBar());
				enemy3CPElement.getElement().setSegments((int)playerData.getEnemy3().maxCommandPoints);
				enemy3CPElement.getElement().setMaxValue(playerData.getEnemy3().maxCommandPoints);
				enemy3CPElement.getElement().setCurrentValue(playerData.getEnemy3().currentCommandPoints);
			}
			
		});
		enemy3CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy3CPElement.getCurrentGameState());
				drawRectangle(bounds.getEnemy3CPBar(), GameConfig.getPlayerColor(playerData.getEnemy3().id));
			}
			
		});
		elements.add(enemy3CPElement);
	}

	private void loadTeamAPBar(UIBounds bounds) {
		GameUIElement<PlainRectangularLoadingBar> teamAPElement = GameUIElement.<PlainRectangularLoadingBar>builder()
				.tag("teamAP")
				.visible(true)
				.element(createAPBar())
				.build();
		teamAPElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				teamAPElement.getElement().setCustomDescriptor("");
				teamAPElement.getElement().setDirection(Direction.NORTH);
			}
			
		});
		teamAPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teamAPElement.getCurrentGameState());
				teamAPElement.setBounds(bounds.getTeamAPBar());
				teamAPElement.getElement().setMaxValue(GameModel.activeGameState.atpToWin);
				teamAPElement.getElement().setCurrentValue(GameModel.activeGameState.teamATPs.get(playerData.getSelf().teamId));
			}
			
		});
		teamAPElement.setPostRenderAction(new Action() {
			
			private float glowAlpha;
			private float alphaStore;
			private float elapsed;
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teamAPElement.getCurrentGameState());
				elapsed += Gdx.graphics.getDeltaTime();
				Planet activeAncientPlanet = null; 
				for (Planet planet : GameModel.activeGameState.planets) {
					if (planet.ancient) {
						activeAncientPlanet = planet;
					}
				}
				Player owner = EngineUtility.getPlayer(GameModel.activeGameState.players, activeAncientPlanet.ownerId);
				if (owner.teamId != playerData.getSelf().teamId && glowAlpha == 0f) {
		        	elapsed = 0f;
		        }
				if (activeAncientPlanet != null) {
			    	if (activeAncientPlanet.ownerId != 0) {
					    Player self = EngineUtility.getPlayer(GameModel.activeGameState.players, GameModel.playerId);
			    		if (owner.teamId == self.teamId) {
			    			alphaStore += Gdx.graphics.getDeltaTime();
			    		}
			    	}
		    	}
				
				float conversion = Gdx.graphics.getDeltaTime();
			    if (elapsed > AP_GLOWUP_DELAY) {
			    	if (alphaStore > 0) {
			    		glowAlpha = MathUtil.clamp(0f, 1f, glowAlpha + conversion);
			    		alphaStore -= conversion;
			    	} else {
			    		glowAlpha = MathUtil.clamp(0f, 1f, glowAlpha - conversion);
			    	}
			    }
				
			    Label apLabel = new Label("AP");
				apLabel.setColor(GameConfig.ancientColor);
				apLabel.setBounds(bounds.getTeamAPBarLabel());
				apLabel.render();
				drawRectangle(bounds.getTeamAPBar(), GameConfig.ancientColor);
				drawRectangle(bounds.getTeamAPBarLabel(), GameConfig.ancientColor);
				drawRectangle(bounds.getTeamAPBar(), GameConfig.ancientColor, glowAlpha);
			}
			
		});
		elements.add(teamAPElement);
	}
	
	private void loadEnemyAPBar(UIBounds bounds) {
		GameUIElement<PlainRectangularLoadingBar> enemyAPElement = GameUIElement.<PlainRectangularLoadingBar>builder()
				.tag("enemyAP")
				.visible(true)
				.element(createAPBar())
				.build();
		enemyAPElement.setInitAction(new Action() {
			
			@Override
			public void execute() {
				enemyAPElement.getElement().setCustomDescriptor("");
				enemyAPElement.getElement().setDirection(Direction.SOUTH);
			}
			
		});
		enemyAPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemyAPElement.getCurrentGameState());
				enemyAPElement.setBounds(bounds.getEnemyAPBar());
				enemyAPElement.getElement().setMaxValue(GameModel.activeGameState.atpToWin);
				enemyAPElement.getElement().setCurrentValue(GameModel.activeGameState.teamATPs.get(playerData.getEnemy1().teamId));
			}
			
		});
		enemyAPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				Label apLabel = new Label("AP");
				apLabel.setColor(GameConfig.ancientColor);
				apLabel.setBounds(bounds.getEnemyAPBarLabel());
				apLabel.render();
				TextureManager.draw(tagPrefix + "game_enemyAPLabel");
				TextureManager.draw(tagPrefix + "game_enemyAP");
				TextureManager.draw(tagPrefix + "game_enemyAPGlow", 0f);
				drawRectangle(bounds.getEnemyAPBar(), GameConfig.ancientColor);
				drawRectangle(bounds.getEnemyAPBarLabel(), GameConfig.ancientColor);
				drawRectangle(bounds.getEnemyAPBar(), GameConfig.ancientColor, 0.7f);
			}
			
		});
		elements.add(enemyAPElement);
	}
	
	private void drawRectangle(Rectangle bounds, Color color) {
		drawRectangle(bounds, color, 1f);
	}
	
	private void drawRectangle(Rectangle bounds, Color color, float alpha) {
		Color modifiedColor = new Color(color.r, color.g, color.b, color.a * alpha);
		Shapes.draw(EQRectangle.builder()
				.bounds(bounds)
				.color(modifiedColor)
				.thickness(2f)
				.build());
	}
	
	private PlainRectangularLoadingBar createAPBar() {
		PlainRectangularLoadingBar apBar = new PlainRectangularLoadingBar();
		apBar.setBar(TextureManager.createTexture(Color.GOLD));
		apBar.setCustomDescriptor("");
		apBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		return apBar;
	}

	private BoxedRectangularLoadingBar createBar() {
		BoxedRectangularLoadingBar bar = new BoxedRectangularLoadingBar();
		bar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		return bar;
	}

}
