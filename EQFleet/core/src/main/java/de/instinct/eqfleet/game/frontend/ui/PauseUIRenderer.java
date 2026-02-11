package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.net.message.types.GamePauseMessage;
import de.instinct.engine.net.message.types.SurrenderMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class PauseUIRenderer {
	
	private ColorButton surrenderButton;
    private ColorButton resumeButton;
    
    public static Color bluroutColor;
	
	public void init() {
		bluroutColor = new Color(0f, 0f, 0f, 0.5f);
		surrenderButton = DefaultButtonFactory.colorButton("Surrender", () -> {
    		SurrenderMessage order = new SurrenderMessage();
    		order.gameUUID = GameModel.activeGameState.gameUUID;
    		order.userUUID = API.authKey;
    		GameModel.outputMessageQueue.add(order);
		});
    	resumeButton = DefaultButtonFactory.colorButton("Resume", () -> {
    		GamePauseMessage order = new GamePauseMessage();
        	order.gameUUID = GameModel.activeGameState.gameUUID;
        	order.userUUID = API.authKey;
        	order.pause = false;
        	GameModel.outputMessageQueue.add(order);
    	});
    	Rectangle surrenderBounds = new Rectangle((GraphicsUtil.screenBounds().width / 2) - 60, 200, 120, 40);
    	surrenderButton.setBounds(surrenderBounds);
        Rectangle resumeBounds = new Rectangle((GraphicsUtil.screenBounds().width / 2) - 60, 100, 120, 40);
        resumeButton.setBounds(resumeBounds);
	}
	
	public void render() {
		GameState state = GameModel.activeGameState;
		renderCountdownScreen(state);
		renderPauseScreen(state);
		if (!state.started) {
			renderLoadingScreen(state);
		}
	}
	
	private void renderCountdownScreen(GameState state) {
		if (state.pauseData.resumeCountdownMS > 0) {
			TextureManager.draw(TextureManager.createTexture(bluroutColor), GraphicsUtil.screenBounds());
			Label pauseLabel = new Label(StringUtils.format(Math.min((state.pauseData.resumeCountdownMS / 1000) + 1, 3), 0));
			pauseLabel.setType(FontType.GIANT);
			pauseLabel.setBounds(new Rectangle(100, (GraphicsUtil.screenBounds().getHeight() / 2), GraphicsUtil.screenBounds().getWidth() - 200, 60));
			pauseLabel.render();
		}
	}

	private void renderPauseScreen(GameState state) {
		if (state.pauseData.teamPause != 0) {
			TextureManager.draw(TextureManager.createTexture(bluroutColor), GraphicsUtil.screenBounds());
			Player self = EngineUtility.getPlayer(state.players, GameModel.playerId);
			String teamName = self.teamId == state.pauseData.teamPause ? "OWN" : "ENEMY";
			Label pauseLabel = new Label("PAUSED - " + teamName + " TEAM");
			pauseLabel.setType(FontType.LARGE);
			pauseLabel.setBounds(new Rectangle(50, (GraphicsUtil.screenBounds().getHeight() / 2) + 200, GraphicsUtil.screenBounds().getWidth() - 100, 60));
			pauseLabel.setBackgroundColor(Color.BLACK);
			pauseLabel.render();
			
			long teamPauseMS = state.pauseData.teamPausesMS.get(state.pauseData.teamPause);
			Label remainingTimeLabel = new Label("Remaining Time: " + StringUtils.format(((float)(state.pauseData.maxPauseMS - teamPauseMS) / 1000f), 0) + "s");
			remainingTimeLabel.setType(FontType.NORMAL);
			remainingTimeLabel.setBounds(new Rectangle(100, (GraphicsUtil.screenBounds().getHeight() / 2) + 100, GraphicsUtil.screenBounds().getWidth() - 200, 30));
			remainingTimeLabel.setBackgroundColor(Color.BLACK);
			remainingTimeLabel.render();
			
			if (self.teamId == state.pauseData.teamPause && state.pauseData.currentPauseElapsedMS > state.pauseData.minPauseMS) {
				surrenderButton.render();
	        	resumeButton.render();
			}
		}
	}

	private void renderLoadingScreen(GameState state) {
		int i = 1;
		float labelHeight = 30;
		Label header = new Label("NAME - CONNECTED - LOADED");
		header.setBounds(new Rectangle(0, 500, GraphicsUtil.screenBounds().getWidth(), labelHeight));
		header.render();
		for (Player player : state.players) {
			if (player.teamId == 0) continue;
			for (PlayerConnectionStatus status : state.connectionStati) {
				if (status.playerId == player.id) {
					Label row = new Label(player.name + " - " + status.connected + " - " + status.loaded);
					row.setBounds(new Rectangle(0, 500 - (i * labelHeight), GraphicsUtil.screenBounds().getWidth(), labelHeight));
					row.render();
					i++;
				}
			}
		}
	}

}
