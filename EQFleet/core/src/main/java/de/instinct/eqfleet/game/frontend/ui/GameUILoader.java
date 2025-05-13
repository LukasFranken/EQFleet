package de.instinct.eqfleet.game.frontend.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.frontend.ui.model.GameUIElement;
import de.instinct.eqfleet.game.frontend.ui.model.PlayerData;
import de.instinct.eqfleet.game.frontend.ui.model.UIBounds;
import de.instinct.eqfleet.menu.module.main.tab.play.PlayTab;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class GameUILoader {
	
	private List<GameUIElement<?>> elements;
	
	private final String tagPrefix = "game_ui_";
	
	private final float AP_GLOWUP_DELAY = 2.8f;
	
	public List<GameUIElement<?>> loadElements(UIBounds bounds) {
		elements = new ArrayList<>();
		
		loadTimeLabel(bounds);
		loadOwnCPBar(bounds);
		loadEnemy1CPBar(bounds);
		if (PlayTab.lobbyStatus.getType().factionMode.teamPlayerCount >= 2) {
			loadTeammate1CPBar(bounds);
			loadEnemy2CPBar(bounds);
		}
		if (PlayTab.lobbyStatus.getType().factionMode.teamPlayerCount >= 3) {
			loadTeammate2CPBar(bounds);
			loadEnemy3CPBar(bounds);
		}
		loadTeamAPBar(bounds);
		loadEnemyAPBar(bounds);
		
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
				createShape(timeElement.getTag(), bounds.getTime(), Color.GRAY);
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
				FontUtil.drawLabel(remainingMS < 60_000 ? Color.RED : Color.WHITE, remainingTimeLabel, bounds.getTime());
				TextureManager.draw(tagPrefix + timeElement.getTag());
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
				PlayerData playerData = UIDataUtility.getPlayerData(ownCPElement.getCurrentGameState());
				createShape(ownCPElement.getTag(), bounds.getOwnCPBar(), GameConfig.getPlayerColor(playerData.getSelf().playerId));
				createShape(ownCPElement.getTag() + "Label", bounds.getOwnCPBarLabel(), GameConfig.getPlayerColor(playerData.getSelf().playerId));
			}
			
		});
		ownCPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(ownCPElement.getCurrentGameState());
				ownCPElement.setBounds(bounds.getOwnCPBar());
				ownCPElement.getElement().setMaxValue(playerData.getSelf().maxCommandPoints);
				ownCPElement.getElement().setCurrentValue(playerData.getSelf().currentCommandPoints);
			}
			
		});
		ownCPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(ownCPElement.getCurrentGameState());
				FontUtil.drawLabel(GameConfig.getPlayerColor(playerData.getSelf().playerId), "CP", bounds.getOwnCPBarLabel());
				TextureManager.draw(tagPrefix + ownCPElement.getTag() + "Label");
				TextureManager.draw(tagPrefix + ownCPElement.getTag());
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
				PlayerData playerData = UIDataUtility.getPlayerData(teammate1CPElement.getCurrentGameState());
				createShape(teammate1CPElement.getTag(), bounds.getTeammate1CPBar(), GameConfig.getPlayerColor(playerData.getTeammate1().playerId));
				createShape(teammate1CPElement.getTag() + "Label", bounds.getTeammate1CPBarLabel(), GameConfig.getPlayerColor(playerData.getTeammate1().playerId));
			}
			
		});
		teammate1CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teammate1CPElement.getCurrentGameState());
				teammate1CPElement.setBounds(bounds.getTeammate1CPBar());
				teammate1CPElement.getElement().setMaxValue(playerData.getTeammate1().maxCommandPoints);
				teammate1CPElement.getElement().setCurrentValue(playerData.getTeammate1().currentCommandPoints);
			}
			
		});
		teammate1CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teammate1CPElement.getCurrentGameState());
				FontUtil.drawLabel(GameConfig.getPlayerColor(playerData.getTeammate1().playerId), "CP", bounds.getTeammate1CPBarLabel());
				TextureManager.draw(tagPrefix + teammate1CPElement.getTag() + "Label");
				TextureManager.draw(tagPrefix + teammate1CPElement.getTag());
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
				PlayerData playerData = UIDataUtility.getPlayerData(teammate2CPElement.getCurrentGameState());
				createShape(teammate2CPElement.getTag(), bounds.getTeammate2CPBar(), GameConfig.getPlayerColor(playerData.getTeammate2().playerId));
				createShape(teammate2CPElement.getTag() + "Label", bounds.getTeammate2CPBarLabel(), GameConfig.getPlayerColor(playerData.getTeammate2().playerId));
			}
			
		});
		teammate2CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teammate2CPElement.getCurrentGameState());
				teammate2CPElement.setBounds(bounds.getTeammate2CPBar());
				teammate2CPElement.getElement().setMaxValue(playerData.getTeammate2().maxCommandPoints);
				teammate2CPElement.getElement().setCurrentValue(playerData.getTeammate2().currentCommandPoints);
			}
			
		});
		teammate2CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teammate2CPElement.getCurrentGameState());
				FontUtil.drawLabel(GameConfig.getPlayerColor(playerData.getTeammate2().playerId), "CP", bounds.getTeammate2CPBarLabel());
				TextureManager.draw(tagPrefix + teammate2CPElement.getTag() + "Label");
				TextureManager.draw(tagPrefix + teammate2CPElement.getTag());
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
				PlayerData playerData = UIDataUtility.getPlayerData(enemy1CPElement.getCurrentGameState());
				createShape(enemy1CPElement.getTag(), bounds.getEnemy1CPBar(), GameConfig.getPlayerColor(playerData.getEnemy1().playerId));
				createShape(enemy1CPElement.getTag() + "Label", bounds.getEnemy1CPBarLabel(), GameConfig.getPlayerColor(playerData.getEnemy1().playerId));
			}
			
		});
		enemy1CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy1CPElement.getCurrentGameState());
				enemy1CPElement.setBounds(bounds.getEnemy1CPBar());
				enemy1CPElement.getElement().setMaxValue(playerData.getEnemy1().maxCommandPoints);
				enemy1CPElement.getElement().setCurrentValue(playerData.getEnemy1().currentCommandPoints);
			}
			
		});
		enemy1CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy1CPElement.getCurrentGameState());
				FontUtil.drawLabel(GameConfig.getPlayerColor(playerData.getEnemy1().playerId), "CP", bounds.getEnemy1CPBarLabel());
				TextureManager.draw(tagPrefix + enemy1CPElement.getTag() + "Label");
				TextureManager.draw(tagPrefix + enemy1CPElement.getTag());
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
				PlayerData playerData = UIDataUtility.getPlayerData(enemy2CPElement.getCurrentGameState());
				createShape(enemy2CPElement.getTag(), bounds.getEnemy2CPBar(), GameConfig.getPlayerColor(playerData.getEnemy2().playerId));
			}
			
		});
		enemy2CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy2CPElement.getCurrentGameState());
				enemy2CPElement.setBounds(bounds.getEnemy2CPBar());
				enemy2CPElement.getElement().setMaxValue(playerData.getEnemy2().maxCommandPoints);
				enemy2CPElement.getElement().setCurrentValue(playerData.getEnemy2().currentCommandPoints);
			}
			
		});
		enemy2CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				TextureManager.draw(tagPrefix + enemy2CPElement.getTag());
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
				PlayerData playerData = UIDataUtility.getPlayerData(enemy3CPElement.getCurrentGameState());
				createShape(enemy3CPElement.getTag(), bounds.getEnemy3CPBar(), GameConfig.getPlayerColor(playerData.getEnemy3().playerId));
			}
			
		});
		enemy3CPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemy3CPElement.getCurrentGameState());
				enemy3CPElement.setBounds(bounds.getEnemy3CPBar());
				enemy3CPElement.getElement().setMaxValue(playerData.getEnemy3().maxCommandPoints);
				enemy3CPElement.getElement().setCurrentValue(playerData.getEnemy3().currentCommandPoints);
			}
			
		});
		enemy3CPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				TextureManager.draw(tagPrefix + enemy3CPElement.getTag());
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
				createShape(teamAPElement.getTag(), bounds.getTeamAPBar(), GameConfig.ancientColor);
				createShape(teamAPElement.getTag() + "Label", bounds.getTeamAPBarLabel(), GameConfig.ancientColor);
				createShape(teamAPElement.getTag() + "Glow", bounds.getTeamAPBar(), GameConfig.ancientColor, 0.7f);
				teamAPElement.getElement().setCustomDescriptor("");
				teamAPElement.getElement().setDirection(Direction.NORTH);
			}
			
		});
		teamAPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(teamAPElement.getCurrentGameState());
				teamAPElement.setBounds(bounds.getTeamAPBar());
				teamAPElement.getElement().setMaxValue(Game.activeGameState.atpToWin);
				teamAPElement.getElement().setCurrentValue(Game.activeGameState.teamATPs.get(playerData.getSelf().teamId));
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
				for (Planet planet : Game.activeGameState.planets) {
					if (planet.ancient) {
						activeAncientPlanet = planet;
					}
				}
				Player owner = EngineUtility.getPlayer(Game.activeGameState, activeAncientPlanet.ownerId);
				if (owner.teamId != playerData.getSelf().teamId && glowAlpha == 0f) {
		        	elapsed = 0f;
		        }
				if (activeAncientPlanet != null) {
			    	if (activeAncientPlanet.ownerId != 0) {
			    		
					    Player self = EngineUtility.getPlayer(Game.activeGameState, Game.playerId);
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
				
				FontUtil.drawLabel(GameConfig.ancientColor, "AP", bounds.getTeamAPBarLabel());
				TextureManager.draw(tagPrefix + teamAPElement.getTag() + "Label");
				TextureManager.draw(tagPrefix + teamAPElement.getTag());
				TextureManager.draw(tagPrefix + teamAPElement.getTag() + "Glow", glowAlpha);
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
				createShape("game_enemyAP", bounds.getEnemyAPBar(), GameConfig.ancientColor);
				createShape("game_enemyAPLabel", bounds.getEnemyAPBarLabel(), GameConfig.ancientColor);
				createShape("game_enemyAPGlow", bounds.getEnemyAPBar(), GameConfig.ancientColor, 0.7f);
				enemyAPElement.getElement().setCustomDescriptor("");
				enemyAPElement.getElement().setDirection(Direction.SOUTH);
			}
			
		});
		enemyAPElement.setUpdateAction(new Action() {
			
			@Override
			public void execute() {
				PlayerData playerData = UIDataUtility.getPlayerData(enemyAPElement.getCurrentGameState());
				enemyAPElement.setBounds(bounds.getEnemyAPBar());
				enemyAPElement.getElement().setMaxValue(Game.activeGameState.atpToWin);
				enemyAPElement.getElement().setCurrentValue(Game.activeGameState.teamATPs.get(playerData.getEnemy1().teamId));
			}
			
		});
		enemyAPElement.setPostRenderAction(new Action() {
			
			@Override
			public void execute() {
				FontUtil.drawLabel(GameConfig.ancientColor, "AP", bounds.getEnemyAPBarLabel());
				TextureManager.draw(tagPrefix + "game_enemyAPLabel");
				TextureManager.draw(tagPrefix + "game_enemyAP");
				TextureManager.draw(tagPrefix + "game_enemyAPGlow", 0f);
			}
			
		});
		elements.add(enemyAPElement);
	}
	
	private void createShape(String tag, Rectangle bounds, Color color) {
		TextureManager.createShapeTexture(tagPrefix + tag,
			    ComplexShapeType.ROUNDED_RECTANGLE,
			    bounds,
			    color);
	}
	
	private void createShape(String tag, Rectangle bounds, Color color, float alpha) {
		TextureManager.createShapeTexture(tagPrefix + tag,
		    ComplexShapeType.ROUNDED_RECTANGLE,
		    bounds,
		    color,
		    alpha);
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
