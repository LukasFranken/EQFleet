package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.menu.module.main.tab.play.PlayTab;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class UIRenderer {
	
	private UIBounds uiBounds;
	
	private float AP_GLOWUP_DELAY = 2.8f;
	
	private float ownGlowAlpha;
	private float enemyGlowAlpha;
	
	private float ownAlphaStore;
	private float enemyAlphaStore;
	private float ownElapsed;
	private float enemyElapsed;
	
	private BoxedRectangularLoadingBar cpLoadingBar;
	private BoxedRectangularLoadingBar teammate1cpLoadingBar;
	private BoxedRectangularLoadingBar teammate2cpLoadingBar;
	private BoxedRectangularLoadingBar enemyCPLoadingBar;
	private BoxedRectangularLoadingBar enemy2CPLoadingBar;
	private BoxedRectangularLoadingBar enemy3CPLoadingBar;
	private PlainRectangularLoadingBar atpLoadingBar;
	private PlainRectangularLoadingBar enemyATPLoadingBar;
	
	private int ancientOwner = 0;
	private Planet activeAncientPlanet;
	
	private UIElementConfig uiElementConfig;
	
	private Player self;
	private Player teammate1;
	private Player teammate2;
	private Player enemy;
	private Player enemy2;
	private Player enemy3;
	
	private boolean initialized;
	
	public void init() {
		uiElementConfig = UIElementConfig.builder()
				.isOwnCPVisible(true)
				.isTeammate1CPVisible(PlayTab.lobbyStatus.getType().factionMode.teamPlayerCount >= 2)
				.isTeammate2CPVisible(PlayTab.lobbyStatus.getType().factionMode.teamPlayerCount >= 3)
				.isEnemyCPVisible(true)
				.isEnemy2CPVisible(PlayTab.lobbyStatus.getType().factionMode.teamPlayerCount >= 2)
				.isEnemy3CPVisible(PlayTab.lobbyStatus.getType().factionMode.teamPlayerCount >= 3)
				.isOwnAPVisible(true)
				.isEnemyAPVisible(true)
				.isTimeVisible(true)
				.build();
		
		activeAncientPlanet = null;
		
		cpLoadingBar = new BoxedRectangularLoadingBar();
		cpLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		teammate1cpLoadingBar = new BoxedRectangularLoadingBar();
		teammate1cpLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		teammate2cpLoadingBar = new BoxedRectangularLoadingBar();
		teammate2cpLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		enemyCPLoadingBar = new BoxedRectangularLoadingBar();
		enemyCPLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		enemy2CPLoadingBar = new BoxedRectangularLoadingBar();
		enemy2CPLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		enemy3CPLoadingBar = new BoxedRectangularLoadingBar();
		enemy3CPLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		atpLoadingBar = new PlainRectangularLoadingBar();
		atpLoadingBar.setBar(TextureManager.createTexture(Color.GOLD));
		atpLoadingBar.setCustomDescriptor("");
		atpLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		enemyATPLoadingBar = new PlainRectangularLoadingBar();
		enemyATPLoadingBar.setBar(TextureManager.createTexture(Color.GOLD));
		enemyATPLoadingBar.setCustomDescriptor("");
		enemyATPLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		ownGlowAlpha = 0f;
		enemyGlowAlpha = 0f;
		ownAlphaStore = 0f;
		enemyAlphaStore = 0f;
		ownElapsed = 0f;
		enemyElapsed = 0f;
		
		ParticleRenderer.loadParticles("ancient", "ancient");
		ParticleRenderer.stop("ancient");
		
		loadPlayers();
		calculateUIBounds();
		createTextures();
		
		initialized = true;
	}
	
	private void loadPlayers() {
		self = EngineUtility.getPlayer(Game.activeGameState, Game.playerId);
		for (Player player : Game.activeGameState.players) {
			if (player.playerId == 0) {
				continue;
			}
			if (player.playerId == self.playerId) {
				continue;
			}
			if (player.teamId == self.teamId) {
				if (teammate1 == null) {
					teammate1 = player;
					continue;
				}
				if (teammate2 == null) {
					teammate2 = player;
					continue;
				}
			} else {
				if (enemy == null) {
					enemy = player;
					continue;
				}
				if (enemy2 == null) {
					enemy2 = player;
					continue;
				}
				if (enemy3 == null) {
					enemy3 = player;
					continue;
				}
			}
		}
	}

	private void calculateUIBounds() {
		float scaleX = Gdx.graphics.getWidth() / 400f;
		float scaleY = Gdx.graphics.getHeight() / 900f;
		
		uiBounds = UIBounds.builder()
				.time(new Rectangle(330 * scaleX, 870 * scaleY, 65 * scaleX, 25 * scaleY))
				.ownCPBar(new Rectangle(47 * scaleX, 18 * scaleY, 330 * scaleX, 27 * scaleY))
				.ownCPBarLabel(new Rectangle(20 * scaleX, 18 * scaleY, 27 * scaleX, 27 * scaleY))
				.teammate1CPBar(new Rectangle(75 * scaleX, (18 + 27 + 3) * scaleY, 135 * scaleX, 20 * scaleY))
				.teammate1CPBarLabel(new Rectangle(50 * scaleX, (18 + 27 + 3) * scaleY, 25 * scaleX, 20 * scaleY))
				.teammate2CPBar(new Rectangle((75 + 155 + 10) * scaleX, (18 + 27 + 3) * scaleY, 135 * scaleX, 20 * scaleY))
				.teammate2CPBarLabel(new Rectangle((50 + 155 + 10) * scaleX, (18 + 27 + 3) * scaleY, 25 * scaleX, 20 * scaleY))
				.enemy1CPBarLabel(new Rectangle((51 + 10) * scaleX, 831 * scaleY, 27 * scaleX, 27 * scaleY))
				.enemy1CPBar(new Rectangle((51 + 27 + 10) * scaleX, 831 * scaleY, 82 * scaleX, 27 * scaleY))
				.enemy2CPBar(new Rectangle((51 + 27 + 10 + 82 + 5) * scaleX, 831 * scaleY, 82 * scaleX, 27 * scaleY))
				.enemy3CPBar(new Rectangle((51 + 27 + 10 + 82 + 5 + 82 + 5) * scaleX, 831 * scaleY, 82 * scaleX, 27 * scaleY))
				.teamAPBar(new Rectangle(20 * scaleX, 174 * scaleY, 27 * scaleX, 207 * scaleY))
				.teamAPBarLabel(new Rectangle(20 * scaleX, 147 * scaleY, 27 * scaleX, 27 * scaleY))
				.enemyAPBar(new Rectangle(20 * scaleX, 492 * scaleY, 27 * scaleX, 207 * scaleY))
				.enemyAPBarLabel(new Rectangle(20 * scaleX, (492 + 207) * scaleY, 27 * scaleX, 27 * scaleY))
				.build();
	}

	private void createTextures() {
		TextureManager.createShapeTexture("game_time", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getTime(),
				Color.GRAY);
		
		TextureManager.createShapeTexture("game_ownCP", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getOwnCPBar(),
				GameConfig.getPlayerColor(self.playerId));
		
		TextureManager.createShapeTexture("game_ownCPLabel", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getOwnCPBarLabel(),
				GameConfig.getPlayerColor(self.playerId));
		
		if (uiElementConfig.isTeammate1CPVisible()) {
			TextureManager.createShapeTexture("game_teammate1CP", 
					ComplexShapeType.ROUNDED_RECTANGLE,
					uiBounds.getTeammate1CPBar(),
					GameConfig.getPlayerColor(teammate1.playerId));
			
			TextureManager.createShapeTexture("game_teammate1CPLabel", 
					ComplexShapeType.ROUNDED_RECTANGLE,
					uiBounds.getTeammate1CPBarLabel(),
					GameConfig.getPlayerColor(teammate1.playerId));
		}
		
		if (uiElementConfig.isTeammate2CPVisible()) {
			TextureManager.createShapeTexture("game_teammate2CP", 
					ComplexShapeType.ROUNDED_RECTANGLE,
					uiBounds.getTeammate2CPBar(),
					GameConfig.getPlayerColor(teammate2.playerId));
			
			TextureManager.createShapeTexture("game_teammate2CPLabel", 
					ComplexShapeType.ROUNDED_RECTANGLE,
					uiBounds.getTeammate2CPBarLabel(),
					GameConfig.getPlayerColor(teammate2.playerId));
		}
		
		TextureManager.createShapeTexture("game_enemy1CPLabel", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getEnemy1CPBarLabel(),
				GameConfig.getPlayerColor(enemy.playerId));
		
		TextureManager.createShapeTexture("game_enemy1CP", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getEnemy1CPBar(),
				GameConfig.getPlayerColor(enemy.playerId));
		
		if (uiElementConfig.isEnemy2CPVisible()) {
			TextureManager.createShapeTexture("game_enemy2CP", 
					ComplexShapeType.ROUNDED_RECTANGLE,
					uiBounds.getEnemy2CPBar(),
					GameConfig.getPlayerColor(enemy2.playerId));
		}
		
		if (uiElementConfig.isEnemy3CPVisible()) {
			TextureManager.createShapeTexture("game_enemy3CP", 
					ComplexShapeType.ROUNDED_RECTANGLE,
					uiBounds.getEnemy3CPBar(),
					GameConfig.getPlayerColor(enemy3.playerId));
		}
		
		TextureManager.createShapeTexture("game_teamAP", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getTeamAPBar(),
				GameConfig.ancientColor);
		
		TextureManager.createShapeTexture("game_teamAPLabel", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getTeamAPBarLabel(),
				GameConfig.ancientColor);
		
		TextureManager.createShapeTexture("game_teamAPGlow", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getTeamAPBar(),
				GameConfig.ancientColor,
				0.7f);
		
		TextureManager.createShapeTexture("game_enemyAP", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getEnemyAPBar(),
				GameConfig.ancientColor);
		
		TextureManager.createShapeTexture("game_enemyAPLabel", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getEnemyAPBarLabel(),
				GameConfig.ancientColor);
		
		TextureManager.createShapeTexture("game_enemyAPGlow", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getEnemyAPBar(),
				GameConfig.ancientColor,
				0.7f);
	}
	
	public void render(GameState state) {
		if (Game.activeGameState != null) {
			if (!initialized) {
				init();
			} else {
				updateUI(state);
				renderUI(state);
			}
		}
	}
	
	private void updateUI(GameState state) {
	    ownElapsed += Gdx.graphics.getDeltaTime();
	    enemyElapsed += Gdx.graphics.getDeltaTime();
	    if (activeAncientPlanet != null) {
	    	if (activeAncientPlanet.ownerId != 0) {
	    		Player owner = EngineUtility.getPlayer(Game.activeGameState, activeAncientPlanet.ownerId);
			    Player self = EngineUtility.getPlayer(Game.activeGameState, Game.playerId);
	    		if (owner.teamId == self.teamId) {
	    			ownAlphaStore += Gdx.graphics.getDeltaTime();
	    		} else {
	    			enemyAlphaStore += Gdx.graphics.getDeltaTime();
	    		}
	    	}
    	}
	    float conversion = Gdx.graphics.getDeltaTime();
	    if (ownElapsed > AP_GLOWUP_DELAY) {
	    	if (ownAlphaStore > 0) {
	    		ownGlowAlpha = MathUtil.clamp(0f, 1f, ownGlowAlpha + conversion);
	    		ownAlphaStore -= conversion;
	    	} else {
	    		ownGlowAlpha = MathUtil.clamp(0f, 1f, ownGlowAlpha - conversion);
	    	}
	    }
	    if (enemyElapsed > AP_GLOWUP_DELAY) {
	    	if (enemyAlphaStore > 0) {
	    		enemyGlowAlpha = MathUtil.clamp(0f, 1f, enemyGlowAlpha + conversion);
	    		enemyAlphaStore -= conversion;
	    	} else {
	    		enemyGlowAlpha = MathUtil.clamp(0f, 1f, enemyGlowAlpha - conversion);
	    	}
	    }
		
	}
	
	private void renderUI(GameState state) {
		long remainingMS = state.maxGameTimeMS - state.gameTimeMS;
		String remainingTimeLabel = StringUtils.generateCountdownLabel(remainingMS, false);
		if (uiElementConfig.isTimeVisible()) {
			FontUtil.drawLabel(remainingMS < 60_000 ? Color.RED : Color.WHITE, remainingTimeLabel, uiBounds.getTime());
			TextureManager.draw("game_time");
		}

		if (uiElementConfig.isOwnCPVisible()) {
			cpLoadingBar.setBounds(uiBounds.getOwnCPBar());
			cpLoadingBar.setMaxValue(self.maxCommandPoints);
			cpLoadingBar.setCurrentValue(self.currentCommandPoints);
			cpLoadingBar.render();
			FontUtil.drawLabel(GameConfig.getPlayerColor(self.playerId), "CP", uiBounds.getOwnCPBarLabel());
			TextureManager.draw("game_ownCPLabel");
			TextureManager.draw("game_ownCP");
		}
		
		if (uiElementConfig.isTeammate1CPVisible()) {
			teammate1cpLoadingBar.setBounds(uiBounds.getTeammate1CPBar());
			teammate1cpLoadingBar.setMaxValue(teammate1.maxCommandPoints);
			teammate1cpLoadingBar.setCurrentValue(teammate1.currentCommandPoints);
			teammate1cpLoadingBar.render();
			FontUtil.drawLabel(GameConfig.getPlayerColor(teammate1.playerId), "CP", uiBounds.getTeammate1CPBarLabel());
			TextureManager.draw("game_teammate1CPLabel");
			TextureManager.draw("game_teammate1CP");
		}
		
		if (uiElementConfig.isTeammate2CPVisible()) {
			teammate2cpLoadingBar.setBounds(uiBounds.getTeammate2CPBar());
			teammate2cpLoadingBar.setMaxValue(teammate2.maxCommandPoints);
			teammate2cpLoadingBar.setCurrentValue(teammate2.currentCommandPoints);
			teammate2cpLoadingBar.render();
			FontUtil.drawLabel(GameConfig.getPlayerColor(teammate2.playerId), "CP", uiBounds.getTeammate2CPBarLabel());
			TextureManager.draw("game_teammate2CPLabel");
			TextureManager.draw("game_teammate2CP");
		}

		if (uiElementConfig.isEnemyCPVisible()) {
			enemyCPLoadingBar.setBounds(uiBounds.getEnemy1CPBar());
			enemyCPLoadingBar.setMaxValue(enemy.maxCommandPoints);
			enemyCPLoadingBar.setCurrentValue(enemy.currentCommandPoints);
			enemyCPLoadingBar.render();
			FontUtil.drawLabel(GameConfig.enemyColor, "CP", uiBounds.getEnemy1CPBarLabel());
			TextureManager.draw("game_enemy1CPLabel");
			TextureManager.draw("game_enemy1CP");
		}
		
		if (uiElementConfig.isEnemy2CPVisible()) {
			enemy2CPLoadingBar.setBounds(uiBounds.getEnemy2CPBar());
			enemy2CPLoadingBar.setMaxValue(enemy2.maxCommandPoints);
			enemy2CPLoadingBar.setCurrentValue(enemy2.currentCommandPoints);
			enemy2CPLoadingBar.render();
			TextureManager.draw("game_enemy2CP");
		}
		
		if (uiElementConfig.isEnemy3CPVisible()) {
			enemy3CPLoadingBar.setBounds(uiBounds.getEnemy3CPBar());
			enemy3CPLoadingBar.setMaxValue(enemy3.maxCommandPoints);
			enemy3CPLoadingBar.setCurrentValue(enemy3.currentCommandPoints);
			enemy3CPLoadingBar.render();
			TextureManager.draw("game_enemy3CP");
		}

		if (uiElementConfig.isOwnAPVisible()) {
			atpLoadingBar.setBounds(uiBounds.getTeamAPBar());
			atpLoadingBar.setDirection(Direction.NORTH);
			atpLoadingBar.setMaxValue(state.atpToWin);
			atpLoadingBar.setCurrentValue(state.teamATPs.get(self.teamId));
			atpLoadingBar.setCustomDescriptor("");
			atpLoadingBar.render();
			FontUtil.drawLabel(GameConfig.ancientColor, "AP", uiBounds.getTeamAPBarLabel());
			TextureManager.draw("game_teamAPLabel");
			TextureManager.draw("game_teamAP");
			TextureManager.draw("game_teamAPGlow", ownGlowAlpha);
		}

		if (uiElementConfig.isEnemyAPVisible()) {
			enemyATPLoadingBar.setBounds(uiBounds.getEnemyAPBar());
			enemyATPLoadingBar.setDirection(Direction.SOUTH);
			enemyATPLoadingBar.setMaxValue(state.atpToWin);
			enemyATPLoadingBar.setCurrentValue(state.teamATPs.get(enemy.teamId));
			enemyATPLoadingBar.render();
			FontUtil.drawLabel(GameConfig.ancientColor, "AP", uiBounds.getEnemyAPBarLabel());
			TextureManager.draw("game_enemyAPLabel");
			TextureManager.draw("game_enemyAP");
			TextureManager.draw("game_enemyAPGlow", enemyGlowAlpha);
		}
	}

	public void renderParticles(PerspectiveCamera camera) {
		if (initialized) {
			for (Planet planet : Game.activeGameState.planets) {
				if (planet.ancient) {
					activeAncientPlanet = planet;
				}
			}
			Player owner = EngineUtility.getPlayer(Game.activeGameState, activeAncientPlanet.ownerId);
			Player self = EngineUtility.getPlayer(Game.activeGameState, Game.playerId);
			if (activeAncientPlanet != null) {
				if (ancientOwner != activeAncientPlanet.ownerId) {
			        if (activeAncientPlanet.ownerId != 0) {
			            ParticleRenderer.start("ancient");
			        } else {
			            ParticleRenderer.stop("ancient");
			        }
			        ancientOwner = owner.teamId;
			        if (ancientOwner == 1) {
			        	ownElapsed = 0f;
			        }
			        if (ancientOwner != 0 && owner.teamId != self.teamId) {
			        	enemyElapsed = 0f;
			        }
			    }
			    Vector3 projected = camera.project(new Vector3(activeAncientPlanet.xPos, activeAncientPlanet.yPos, 0));
			    Vector2 source = new Vector2(projected.x, projected.y);
			    Vector2 target = (owner.teamId == self.teamId)
			        ? new Vector2(50, 260)
			        : new Vector2(50, 600);

			    Vector2 dir = new Vector2(target).sub(source);
			    float angle = dir.angleDeg();
			    ParticleRenderer.setEmitterAngle("ancient", angle);

			    float distance = dir.len();
			    float baseVelocity = 200f;
			    float targetVelocity = baseVelocity * (distance / 600f);
			    ParticleRenderer.setEmitterVelocity("ancient", targetVelocity);
			    ParticleRenderer.renderParticles("ancient", source);
			}
		}
	}

	public UIElementConfig getElementConfig() {
		return uiElementConfig;
	}

}
