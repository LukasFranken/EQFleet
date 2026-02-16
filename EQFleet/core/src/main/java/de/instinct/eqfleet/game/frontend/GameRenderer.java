package de.instinct.eqfleet.game.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.guide.GuideRenderer;
import de.instinct.eqfleet.game.frontend.planet.PlanetRenderer;
import de.instinct.eqfleet.game.frontend.projectile.ProjectileRenderer;
import de.instinct.eqfleet.game.frontend.ships.ShipRenderer;
import de.instinct.eqfleet.game.frontend.ui.GameUIRenderer;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;

public class GameRenderer {

	public static final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 4400f);
	//private final Vector3 BASE_CAM_POS = new Vector3(0f, -2200f, 2500f);
	private PerspectiveCamera camera;
	
	private GridRenderer gridRenderer;
	private GameUIRenderer uiRenderer;
	
	private PlanetRenderer planetRenderer;
	private ShipRenderer shipRenderer;
	private ProjectileRenderer projectileRenderer;
	
	private GuideRenderer guideRenderer;
	
	private float startZoomOutFactor = 1.5f;
	private float zoomInTime = 3f;
	private float zoomInElapsed;
	
	public static boolean isFlipped;

	public void init() {
		GameModel.mode = InteractionMode.UNIT_CONTROL;
		isFlipped = false;
		
		camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(BASE_CAM_POS);
		camera.lookAt(0f, 0f, 0f);
		camera.up.set(0f, 1f, 0f);
		camera.near = 1f;
		camera.far = 10000f;
		camera.update();

		gridRenderer = new GridRenderer(GridConfiguration.builder().build());
		projectileRenderer = new ProjectileRenderer();
		planetRenderer = new PlanetRenderer();
		shipRenderer = new ShipRenderer();
		
		guideRenderer = new GuideRenderer();
		
		zoomInElapsed = 0f;
		uiRenderer = new GameUIRenderer();
	}

	public void render(GameState state) {
		if (uiRenderer != null) {
			Profiler.startFrame("GAME_RNDR");
			if (!uiRenderer.initialized) {
				uiRenderer.init();
				Profiler.checkpoint("GAME_RNDR", "init");
			}
			camera.update();
			Profiler.checkpoint("GAME_RNDR", "camera");
			if (GameModel.visible) {
				if (zoomInElapsed < zoomInTime) {
					zoomInElapsed += Gdx.graphics.getDeltaTime();
					calculateZoomInStart(state);
					Profiler.checkpoint("GAME_RNDR", "zoom in");
				}
				uiRenderer.setCamera(camera);
				uiRenderer.setState(state);
				if (state != null && state.resultData.winner == 0) {
					if (state.started) {
						Profiler.checkpoint("GAME_RNDR", "pre-render");
						checkFlip();
						gridRenderer.drawGrid(camera);
						Profiler.checkpoint("GAME_RNDR", "grid");
						planetRenderer.render(state, camera);
						Profiler.checkpoint("GAME_RNDR", "planet");
						shipRenderer.render(state, camera);
						Profiler.checkpoint("GAME_RNDR", "ship");
						projectileRenderer.render(state, camera);
						Profiler.checkpoint("GAME_RNDR", "projectile");
					}
				}
				uiRenderer.render();
				Profiler.checkpoint("GAME_RNDR", "ui render");
			}
			guideRenderer.renderEvents(camera);
			Profiler.checkpoint("GAME_RNDR", "events");
			Profiler.endFrame("GAME_RNDR");
		}
	}

	private void calculateZoomInStart(GameState state) {
		float currentZoomOutFactor = MathUtil.easeInOut(startZoomOutFactor, 1f, zoomInElapsed / zoomInTime);
		camera.position.set(new Vector3(BASE_CAM_POS.x, BASE_CAM_POS.y, (BASE_CAM_POS.z / (state.staticData.zoomFactor == 0 ? 1 : state.staticData.zoomFactor)) * currentZoomOutFactor));
	}

	private void checkFlip() {
		Player self = EngineUtility.getPlayer(GameModel.activeGameState.staticData.playerData.players, GameModel.playerId);
		if (isFlipped && self.teamId == 1) {
			flip();
		}
		if (!isFlipped && self.teamId == 2) {
			flip();
		}
	}

	public void flip() {
		camera.rotate(180f, 0f, 0f, 1f);
		isFlipped = !isFlipped;
	}

	public void setUIElementVisible(String tag, boolean visible) {
		uiRenderer.setElementVisible(tag, visible);
	}

	public void dispose() {
		
	}
	
}