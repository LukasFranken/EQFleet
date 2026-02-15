package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.GameRenderer;
import de.instinct.eqfleet.game.frontend.input.GameInputManager;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQArc;

public class GameUIRenderer {
	
	public boolean initialized;
	
	private PerspectiveCamera camera;
	private GameState state;
	
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
				Profiler.startFrame("GAME_UI_RENDERER");
				staticUIRenderer.render();
				Profiler.checkpoint("GAME_UI_RENDERER", "staticUI");
				inputManager.handleInput(camera, state);
				Profiler.checkpoint("GAME_UI_RENDERER", "handleInput");
				renderResourceCircles();
				Profiler.checkpoint("GAME_UI_RENDERER", "resourceCircles");
				defenseUIRenderer.render(state, camera);
				Profiler.checkpoint("GAME_UI_RENDERER", "defenseUI");
				modeUIRenderer.render(camera);
				Profiler.checkpoint("GAME_UI_RENDERER", "modeUI");
				particleUIRenderer.render(camera);
				Profiler.checkpoint("GAME_UI_RENDERER", "particleUI");
				pauseUIRenderer.render();
				Profiler.checkpoint("GAME_UI_RENDERER", "pause render");
				Profiler.endFrame("GAME_UI_RENDERER");
			}
		}
		renderMessageText();
	}
	
	private void renderResourceCircles() {
		for (Planet planet : state.entityData.planets) {
		    Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
		    renderResourceCircle(planet.position.x, planet.position.y, GameConfig.getPlayerColor(owner.id), (float)(planet.currentResources / owner.planetData.maxResourceCapacity));
		}
	}
	
	private void renderResourceCircle(float x, float y, Color color, float value) {
		float radius = EngineUtility.PLANET_RADIUS + 5f;
		float thickness = 8f;
		Shapes.draw(EQArc.builder()
				.position(new Vector2(x, y))
				.innerRadius(radius)
				.outerRadius(radius + thickness)
				.color(color)
				.startAngle(90 + (GameRenderer.isFlipped ? 180 : 0))
				.degrees(value * 360f)
				.projectionMatrix(camera.combined)
				.build());
	}

	private void renderMessageText() {
		String message = "Connecting...";
		if (GameModel.activeGameState != null) {
			int winner = GameModel.activeGameState.resultData.winner;
			if (winner != 0 && !GameModel.activeGameState.gameUUID.equals("tutorial")) {
				if (winner == EngineUtility.getPlayer(GameModel.activeGameState.staticData.playerData.players, GameModel.playerId).teamId) {
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

	public void setState(GameState state) {
		this.state = state;
	}
	
	public void setElementVisible(String tag, boolean visible) {
		staticUIRenderer.setElementVisible(tag, visible);
	}
	
}
