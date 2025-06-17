package de.instinct.eqfleet.game.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.guide.GuideRenderer;
import de.instinct.eqfleet.game.frontend.planet.PlanetRenderer;
import de.instinct.eqfleet.game.frontend.projectile.ProjectileRenderer;
import de.instinct.eqfleet.game.frontend.ships.ShipRenderer;
import de.instinct.eqfleet.game.frontend.ui.GameUIRenderer;
import de.instinct.eqlibgdxutils.rendering.GridRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class GameRenderer {

	private final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 4400f);
	private PerspectiveCamera camera;
	
	private GridRenderer gridRenderer;
	private GameUIRenderer uiRenderer;
	
	private PlanetRenderer planetRenderer;
	private ShipRenderer shipRenderer;
	private ProjectileRenderer projectileRenderer;
	
	private GuideRenderer guideRenderer;
	
	public boolean visible;
	
	public static boolean isFlipped;

	public void init() {
		isFlipped = false;
		uiRenderer = new GameUIRenderer();
		
		camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(BASE_CAM_POS);
		camera.lookAt(0f, 0f, 0f);
		camera.up.set(0f, 1f, 0f);
		camera.near = 1f;
		camera.far = 5000f;
		camera.update();

		gridRenderer = new GridRenderer();
		projectileRenderer = new ProjectileRenderer();
		planetRenderer = new PlanetRenderer();
		shipRenderer = new ShipRenderer();
		
		guideRenderer = new GuideRenderer();
		visible = true;
	}

	public void render(GameState state) {
		if (state != null && !uiRenderer.initialized) {
			uiRenderer.init();
		}
		if (visible) {
			camera.update();

			if (state != null && state.winner == 0) {
				if (state.started) {
					checkFlip();
					gridRenderer.drawGrid(camera);
					uiRenderer.renderParticles(camera);
					planetRenderer.render(state, camera);
					shipRenderer.render(state, camera);
					projectileRenderer.render(state, camera);
				} else {
					renderLoadingScreen(state);
				}
			}
			uiRenderer.render(state, camera);
			guideRenderer.renderEvents(camera);
		}
	}

	private void renderLoadingScreen(GameState state) {
		int i = 1;
		float labelHeight = 30;
		Label header = new Label("NAME - CONNECTED - LOADED");
		header.setBounds(new Rectangle(0, 500, Gdx.graphics.getWidth(), labelHeight));
		header.render();
		for (Player player : state.players) {
			if (player.teamId == 0) continue;
			for (PlayerConnectionStatus status : state.connectionStati) {
				if (status.playerId == player.id) {
					Label row = new Label(player.name + " - " + status.connected + " - " + status.loaded);
					row.setBounds(new Rectangle(0, 500 - (i * labelHeight), Gdx.graphics.getWidth(), labelHeight));
					row.render();
					i++;
				}
			}
		}
	}

	private void checkFlip() {
		Player self = EngineUtility.getPlayer(GameModel.activeGameState.players, GameModel.playerId);
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