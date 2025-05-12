package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class UIRenderer {
	
	private UIBounds uiBounds;
	
	public UIRenderer() {
		calculateUIBounds();
		createTextures();
	}
	
	private void calculateUIBounds() {
		float scaleX = Gdx.graphics.getWidth() / 400f;
		float scaleY = Gdx.graphics.getHeight() / 900f;
		
		uiBounds = UIBounds.builder()
				.ownCPBar(new Rectangle(51 * scaleX, 18 * scaleY, 330 * scaleX, 25 * scaleY))
				.enemy1CPBar(new Rectangle(51 * scaleX, 831 * scaleY, 82 * scaleX, 26 * scaleY))
				.teamAPBar(new Rectangle(20 * scaleX, 74 * scaleY, 27 * scaleX, 207 * scaleY))
				.enemyAPBar(new Rectangle(20 * scaleX, 592 * scaleY, 27 * scaleX, 207 * scaleY))
				.build();
	}

	private void createTextures() {
		TextureManager.createShapeTexture("game_ownCP", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getOwnCPBar(),
				teammate1Color);
		
		TextureManager.createShapeTexture("game_enemy1CP", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getEnemy1CPBar(),
				enemyColor);
		
		TextureManager.createShapeTexture("game_teamAP", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getTeamAPBar(),
				ancientColor);
		
		TextureManager.createShapeTexture("game_teamAPGlow", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getTeamAPBar(),
				ancientColor,
				0.9f);
		
		TextureManager.createShapeTexture("game_enemyAP", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getEnemyAPBar(),
				ancientColor);
		
		TextureManager.createShapeTexture("game_enemyAPGlow", 
				ComplexShapeType.ROUNDED_RECTANGLE,
				uiBounds.getEnemyAPBar(),
				ancientColor,
				0.9f);
	}
	
	public void render(GameState state) {
		updateUI();
		renderUI();
	}
	
	private void updateUI() {
	    Rectangle overlayBounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
		
	    if (config.getUiElementConfig().isTimeVisible()) {
	    	TextureManager.draw(TextureManager.getTexture("ui/image", "game_ui_layout_time"), overlayBounds);
	    }
	}
	
	private void renderUI(GameState state) {
		Player self = EngineUtility.getPlayer(state, Game.playerId);
		Player enemy1 = state.players.stream().filter(player -> player.teamId != 0 && player.teamId != self.teamId).findFirst().orElse(null);

		float scaleX = Gdx.graphics.getWidth() / 400f;
		float scaleY = Gdx.graphics.getHeight() / 900f;

		long remainingMS = state.maxGameTimeMS - state.gameTimeMS;
		String remainingTimeLabel = StringUtils.generateCountdownLabel(remainingMS, false);
		if (config.getUiElementConfig().isTimeVisible()) FontUtil.draw(remainingMS < 60_000 ? Color.RED : Color.WHITE, remainingTimeLabel, 291 * scaleX, 850 * scaleY);

		cpLoadingBar.setBounds(uiBounds.getOwnCPBar());
		cpLoadingBar.setMaxValue(self.maxCommandPoints);
		cpLoadingBar.setCurrentValue(self.currentCommandPoints);
		if (config.getUiElementConfig().isOwnCPVisible()) {
			cpLoadingBar.render();
			TextureManager.draw("game_ownCP");
		}

		enemyCPLoadingBar.setBounds(uiBounds.getEnemy1CPBar());
		enemyCPLoadingBar.setMaxValue(enemy1.maxCommandPoints);
		enemyCPLoadingBar.setCurrentValue(enemy1.currentCommandPoints);
		if (config.getUiElementConfig().isEnemyCPVisible()) {
			enemyCPLoadingBar.render();
			TextureManager.draw("game_enemy1CP");
		}

		atpLoadingBar.setBounds(uiBounds.getTeamAPBar());
		atpLoadingBar.setDirection(Direction.NORTH);
		atpLoadingBar.setMaxValue(state.atpToWin);
		atpLoadingBar.setCurrentValue(state.teamATPs.get(self.teamId));
		atpLoadingBar.setCustomDescriptor("");
		if (config.getUiElementConfig().isOwnAPVisible()) {
			atpLoadingBar.render();
			TextureManager.draw("game_teamAP");
			TextureManager.draw("game_teamAPGlow", ownGlowAlpha);
		}

		enemyATPLoadingBar.setBounds(uiBounds.getEnemyAPBar());
		enemyATPLoadingBar.setDirection(Direction.SOUTH);
		enemyATPLoadingBar.setMaxValue(state.atpToWin);
		enemyATPLoadingBar.setCurrentValue(state.teamATPs.get(enemy1.teamId));
		if (config.getUiElementConfig().isEnemyAPVisible()) {
			enemyATPLoadingBar.render();
			TextureManager.draw("game_enemyAP");
			TextureManager.draw("game_enemyAPGlow", enemyGlowAlpha);
		}
	}

}
