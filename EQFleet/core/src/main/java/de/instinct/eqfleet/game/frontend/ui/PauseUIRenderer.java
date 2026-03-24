package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.graphics.Color;

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
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class PauseUIRenderer {
	
	private ColorButton surrenderButton;
    private ColorButton resumeButton;
    private EQRectangle bluroutShape;
    private Label workingLabel;
	
	public void init() {
		workingLabel = new Label("");
		bluroutShape = EQRectangle.builder()
				.bounds(GraphicsUtil.screenBounds())
				.color(new Color(0f, 0f, 0f, 0.7f))
				.filled(true)
				.build();
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
			Shapes.draw(bluroutShape);
			workingLabel.setText(StringUtils.format(Math.min((state.pauseData.resumeCountdownMS / 1000) + 1, 3), 0));
			workingLabel.setType(FontType.GIANT);
			workingLabel.setBounds(100, (GraphicsUtil.screenBounds().getHeight() / 2), GraphicsUtil.screenBounds().getWidth() - 200, 60);
			workingLabel.render();
		}
	}

	private void renderPauseScreen(GameState state) {
		if (state.pauseData.teamPause != 0) {
			Shapes.draw(bluroutShape);
			Player self = EngineUtility.getPlayer(state.staticData.playerData.players, GameModel.playerId);
			String teamName = self.teamId == state.pauseData.teamPause ? "OWN" : "ENEMY";
			workingLabel.setText("PAUSED - " + teamName + " TEAM");
			workingLabel.setType(FontType.LARGE);
			workingLabel.setBounds(50, (GraphicsUtil.screenBounds().getHeight() / 2) + 200, GraphicsUtil.screenBounds().getWidth() - 100, 60);
			workingLabel.setBackgroundColor(Color.BLACK);
			workingLabel.render();
			
			long teamPauseMS = state.pauseData.teamPausesMS.get(state.pauseData.teamPause);
			workingLabel.setText("Remaining Time: " + StringUtils.format(((float)(state.staticData.maxPauseMS - teamPauseMS) / 1000f), 0) + "s");
			workingLabel.setType(FontType.NORMAL);
			workingLabel.setBounds(100, (GraphicsUtil.screenBounds().getHeight() / 2) + 100, GraphicsUtil.screenBounds().getWidth() - 200, 30);
			workingLabel.setBackgroundColor(Color.BLACK);
			workingLabel.render();
			
			if (self.teamId == state.pauseData.teamPause && state.pauseData.currentPauseElapsedMS > state.staticData.minPauseMS) {
				surrenderButton.setBounds((GraphicsUtil.screenBounds().width / 2) - 60, 180, 120, 40);
		        resumeButton.setBounds((GraphicsUtil.screenBounds().width / 2) - 60, 100, 120, 40);
				surrenderButton.render();
	        	resumeButton.render();
			}
		}
	}

	private void renderLoadingScreen(GameState state) {
		int i = 1;
		float labelHeight = 30;
		workingLabel.setText("NAME - CONNECTED - LOADED");
		workingLabel.setType(FontType.NORMAL);
		workingLabel.setBounds(0, 500, GraphicsUtil.screenBounds().getWidth(), labelHeight);
		workingLabel.render();
		for (Player player : state.staticData.playerData.players) {
			if (player.teamId == 0) continue;
			for (PlayerConnectionStatus status : state.staticData.playerData.connectionStati) {
				if (status.playerId == player.id) {
					workingLabel.setText(player.name + " - " + status.connected + " - " + status.loaded);
					workingLabel.setBounds(0, 500 - (i * labelHeight), GraphicsUtil.screenBounds().getWidth(), labelHeight);
					workingLabel.render();
					i++;
				}
			}
		}
	}

}
