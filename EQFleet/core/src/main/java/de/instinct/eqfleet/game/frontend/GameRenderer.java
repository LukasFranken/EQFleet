package de.instinct.eqfleet.game.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

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
	
	public static boolean isFlipped;
	public static Color bluroutColor;

	public void init() {
		isFlipped = false;
		uiRenderer = new GameUIRenderer();
		
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
		
		bluroutColor = new Color(0f, 0f, 0f, 0.5f);
	}

	public void render(GameState state) {
		if (state != null && !uiRenderer.initialized) {
			uiRenderer.init();
		}
		camera.update();
		if (GameModel.visible) {
			uiRenderer.setCamera(camera);
			uiRenderer.setState(state);
			if (state != null && state.winner == 0) {
				if (state.started) {
					checkFlip();
					gridRenderer.drawGrid(camera);
					uiRenderer.renderParticles();
					planetRenderer.render(state, camera);
					shipRenderer.render(state, camera);
					projectileRenderer.render(state, camera);
				} else {
					renderLoadingScreen(state);
				}
			}
			uiRenderer.render();
			if (state != null && state.winner == 0) {
				renderCountdownScreen(state);
				renderPauseScreen(state);
			}
		}
		guideRenderer.renderEvents(camera);
	}

	private void renderCountdownScreen(GameState state) {
		if (state.resumeCountdownMS > 0) {
			TextureManager.draw(TextureManager.createTexture(bluroutColor), GraphicsUtil.screenBounds());
			Label pauseLabel = new Label(StringUtils.format(Math.min((state.resumeCountdownMS / 1000) + 1, 3), 0));
			pauseLabel.setType(FontType.GIANT);
			pauseLabel.setBounds(new Rectangle(100, (Gdx.graphics.getHeight() / 2), Gdx.graphics.getWidth() - 200, 60));
			pauseLabel.render();
		}
	}

	private void renderPauseScreen(GameState state) {
		if (state.teamPause != 0) {
			TextureManager.draw(TextureManager.createTexture(bluroutColor), GraphicsUtil.screenBounds());
			Player self = EngineUtility.getPlayer(state.players, GameModel.playerId);
			String teamName = self.teamId == state.teamPause ? "OWN" : "ENEMY";
			Label pauseLabel = new Label("PAUSED - " + teamName + " TEAM");
			pauseLabel.setType(FontType.LARGE);
			pauseLabel.setBounds(new Rectangle(50, (Gdx.graphics.getHeight() / 2) + 200, Gdx.graphics.getWidth() - 100, 60));
			pauseLabel.setBackgroundColor(Color.BLACK);
			pauseLabel.render();
			
			long teamPauseMS = state.teamPausesMS.get(state.teamPause);
			Label remainingTimeLabel = new Label("Remaining Time: " + StringUtils.format(((float)(state.maxPauseMS - teamPauseMS) / 1000f), 0) + "s");
			remainingTimeLabel.setType(FontType.NORMAL);
			remainingTimeLabel.setBounds(new Rectangle(100, (Gdx.graphics.getHeight() / 2) + 100, Gdx.graphics.getWidth() - 200, 30));
			remainingTimeLabel.setBackgroundColor(Color.BLACK);
			remainingTimeLabel.render();
			
			if (self.teamId == state.teamPause && state.currentPauseElapsedMS > state.minPauseMS) {
				Border buttonBorder = new Border();
				buttonBorder.setColor(new Color(SkinManager.skinColor));
				buttonBorder.setSize(2f);
				
				Rectangle surrenderBounds = new Rectangle((Gdx.graphics.getWidth() / 2) - 50, 200, 100, 40);
				Label surrenderLabel = new Label("Surrender");
				surrenderLabel.setType(FontType.NORMAL);
				surrenderLabel.setBounds(surrenderBounds);
				surrenderLabel.setBackgroundColor(Color.BLACK);
				surrenderLabel.setBorder(buttonBorder);
				surrenderLabel.render();
				
				Rectangle resumeBounds = new Rectangle((Gdx.graphics.getWidth() / 2) - 50, 100, 100, 40);
				Label resumeLabel = new Label("Resume");
				resumeLabel.setType(FontType.NORMAL);
				resumeLabel.setBounds(resumeBounds);
				resumeLabel.setBackgroundColor(Color.BLACK);
				resumeLabel.setBorder(buttonBorder);
				resumeLabel.render();
			}
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