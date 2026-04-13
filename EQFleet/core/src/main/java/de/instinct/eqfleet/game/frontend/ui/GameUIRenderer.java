package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.instinct.api.core.API;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.net.messages.LoadedMessage;
import de.instinct.engine_api.core.service.EngineDataInterface;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.input.GameInputManager;
import de.instinct.eqfleet.game.frontend.ui.defense.DefenseUIRenderer;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class GameUIRenderer {
	
	public boolean initialized;
	
	private PerspectiveCamera camera;
	private FleetGameState state;
	
	private GameInputManager inputManager;
	
	private DefenseUIRenderer defenseUIRenderer;
    private StaticUIRenderer staticUIRenderer;
    private PauseUIRenderer pauseUIRenderer;
    private GameUIParticleRenderer particleUIRenderer;
    private ModeUIRenderer modeUIRenderer;
	
	public GameUIRenderer() {
		ParticleRenderer.loadParticles("ancient", "ancient");
		ParticleRenderer.stop("ancient");
		inputManager = new GameInputManager();
		defenseUIRenderer = new DefenseUIRenderer();
		particleUIRenderer = new GameUIParticleRenderer();
		staticUIRenderer = new StaticUIRenderer();
		pauseUIRenderer = new PauseUIRenderer();
		modeUIRenderer = new ModeUIRenderer();
	}
	
	public void init() {
		staticUIRenderer.init();
		modeUIRenderer.init();
		pauseUIRenderer.init();
		LoadedMessage loadedMessage = new LoadedMessage();
		loadedMessage.playerUUID = API.authKey;
		GameModel.outputMessageQueue.add(loadedMessage);
		initialized = true;
	}
	
	public void render() {
		if (state != null) {
			if (initialized && state.resultData.winner == 0) {
				Profiler.startFrame("GAME_UI_RNDR");
				inputManager.handleInput(camera, state);
				Profiler.checkpoint("GAME_UI_RNDR", "input");
				defenseUIRenderer.render(state, camera);
				Profiler.checkpoint("GAME_UI_RNDR", "defenseUI");
				staticUIRenderer.render();
				Profiler.checkpoint("GAME_UI_RNDR", "staticUI");
				modeUIRenderer.render(camera);
				Profiler.checkpoint("GAME_UI_RNDR", "modeUI");
				particleUIRenderer.render(camera);
				Profiler.checkpoint("GAME_UI_RNDR", "particleUI");
				pauseUIRenderer.render();
				Profiler.checkpoint("GAME_UI_RNDR", "pause");
				Profiler.endFrame("GAME_UI_RNDR");
			}
		}
		renderMessageText();
	}

	private void renderMessageText() {
		String message = "Connecting...";
		if (GameModel.activeGameState != null) {
			int winner = GameModel.activeGameState.resultData.winner;
			if (winner != 0 && !GameModel.activeGameState.gameUUID.equals("tutorial")) {
				if (winner == EngineDataInterface.getPlayer(GameModel.activeGameState.playerData.players, GameModel.playerId).teamId) {
					if (GameModel.activeGameState.resultData.wiped) {
						message = "DOMINATION";
					} else {
						message = "VICTORY";
					}
				} else if (winner != 0) {
					if (GameModel.activeGameState.resultData.surrendered != 0) {
						message = "SURRENDERED";
					} else {
						if (GameModel.activeGameState.resultData.wiped) {
							message = "WIPED OUT";
						} else {
							message = "DEFEATED";
						}
					}
				}
			} else {
				message = "";
			}
		}
		Label messageLabel = new Label(message);
		messageLabel.setColor(Color.WHITE);
		messageLabel.setType(FontType.LARGE);
		messageLabel.setBounds(GraphicsUtil.screenBounds());
		messageLabel.render();
	}

	public void setCamera(PerspectiveCamera camera) {
		this.camera = camera;
	}

	public void setState(FleetGameState state) {
		this.state = state;
	}
	
	public void setElementVisible(String tag, boolean visible) {
		staticUIRenderer.setElementVisible(tag, visible);
	}
	
}
