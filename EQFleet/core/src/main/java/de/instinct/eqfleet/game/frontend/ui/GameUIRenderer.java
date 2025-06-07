package de.instinct.eqfleet.game.frontend.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.core.API;
import de.instinct.engine.combat.Combat;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.ApplicationMode;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.GameInputManager;
import de.instinct.eqfleet.game.frontend.PlanetPair;
import de.instinct.eqfleet.game.frontend.ui.model.GameUIElement;
import de.instinct.eqfleet.game.frontend.ui.model.UIBounds;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeRenderer;

public class GameUIRenderer {
	
	public boolean initialized;
	
	private GameUILoader uiLoader;
	private List<GameUIElement<?>> elements;
	
	private UIBounds bounds;
	
	private ShapeRenderer shapeRenderer;
	private ComplexShapeRenderer complexShapeRenderer;
	private SpriteBatch batch;
	private GameInputManager inputManager;
	private DefenseUIRenderer defenseUIRenderer;
	
	public GameUIRenderer() {
		uiLoader = new GameUILoader();
		ParticleRenderer.loadParticles("ancient", "ancient");
		ParticleRenderer.stop("ancient");
		shapeRenderer = new ShapeRenderer();
		complexShapeRenderer = new ComplexShapeRenderer();
		batch = new SpriteBatch();
		inputManager = new GameInputManager();
		defenseUIRenderer = new DefenseUIRenderer();
	}
	
	public void init() {
		loadBounds();
		elements = uiLoader.loadElements(bounds);
		initializeElements();
		
		LoadedMessage loadedMessage = new LoadedMessage();
		loadedMessage.playerUUID = API.authKey;
		GameModel.outputMessageQueue.add(loadedMessage);
		initialized = true;
	}

	private void initializeElements() {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.getInitAction() != null) {
				gameUIelement.setCurrentGameState(GameModel.activeGameState);
				gameUIelement.getInitAction().execute();
			}
		}
	}

	private void loadBounds() {
		float scaleX = Gdx.graphics.getWidth() / 400f;
		float scaleY = Gdx.graphics.getHeight() / 900f;
		
		bounds = UIBounds.builder()
		.time(new Rectangle(330 * scaleX, 830 * scaleY, 65 * scaleX, 25 * scaleY))
		.ownCPBar(new Rectangle(47 * scaleX, 18 * scaleY, 330 * scaleX, 27 * scaleY))
		.ownCPBarLabel(new Rectangle(20 * scaleX, 18 * scaleY, 27 * scaleX, 27 * scaleY))
		.teammate1CPBar(new Rectangle(75 * scaleX, (18 + 27 + 3) * scaleY, 135 * scaleX, 20 * scaleY))
		.teammate1CPBarLabel(new Rectangle(50 * scaleX, (18 + 27 + 3) * scaleY, 25 * scaleX, 20 * scaleY))
		.teammate2CPBar(new Rectangle((75 + 155 + 10) * scaleX, (18 + 27 + 3) * scaleY, 135 * scaleX, 20 * scaleY))
		.teammate2CPBarLabel(new Rectangle((50 + 155 + 10) * scaleX, (18 + 27 + 3) * scaleY, 25 * scaleX, 20 * scaleY))
		.enemy1CPBarLabel(new Rectangle((51 + 10) * scaleX, 800 * scaleY, 27 * scaleX, 27 * scaleY))
		.enemy1CPBar(new Rectangle((51 + 27 + 10) * scaleX, 800 * scaleY, 82 * scaleX, 27 * scaleY))
		.enemy2CPBar(new Rectangle((51 + 27 + 10 + 82 + 5) * scaleX, 800 * scaleY, 82 * scaleX, 27 * scaleY))
		.enemy3CPBar(new Rectangle((51 + 27 + 10 + 82 + 5 + 82 + 5) * scaleX, 800 * scaleY, 82 * scaleX, 27 * scaleY))
		.teamAPBar(new Rectangle(20 * scaleX, 174 * scaleY, 27 * scaleX, 207 * scaleY))
		.teamAPBarLabel(new Rectangle(20 * scaleX, 147 * scaleY, 27 * scaleX, 27 * scaleY))
		.enemyAPBar(new Rectangle(20 * scaleX, 492 * scaleY, 27 * scaleX, 207 * scaleY))
		.enemyAPBarLabel(new Rectangle(20 * scaleX, (492 + 207) * scaleY, 27 * scaleX, 27 * scaleY))
		.build();
	}
	
	public void render(GameState state, PerspectiveCamera camera) {
		if (GameModel.activeGameState != null) {
			if (initialized && state.winner == 0) {
				inputManager.handleInput(camera, state);
				updateStaticUI(state);
				renderStaticUI(state);
				defenseUIRenderer.render(state, camera);
				renderResourceCircles(state, camera);
				renderFleetConnections(state);
				renderSelection(state, camera);
			}
		}
		renderMessageText();
	}

	private void renderResourceCircles(GameState state, PerspectiveCamera camera) {
		for (Planet planet : state.planets) {
			Vector3 screenPos = camera.project(new Vector3(planet.position.x, planet.position.y, 0f));
		    String value = String.valueOf((int) planet.currentResources);
		    float labelWidth = FontUtil.getFontTextWidthPx(value);
		    float labelHeight = FontUtil.getFontHeightPx();
		    float labelX = screenPos.x - labelWidth / 2f;
		    float labelY = screenPos.y - labelHeight / 2f;
		    
		    Player owner = EngineUtility.getPlayer(state.players, planet.ownerId);
		    renderResourceCircle(planet.position.x, planet.position.y, GameConfig.getPlayerColor(owner.id), (float)(planet.currentResources / owner.planetData.maxResourceCapacity), camera);

		    if (GlobalStaticData.mode == ApplicationMode.DEV) {
		    	shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			    Gdx.gl.glEnable(GL20.GL_BLEND);
			    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			    shapeRenderer.setColor(0f, 0f, 0f, 0.7f);
			    shapeRenderer.rect(labelX - 4, labelY - 2, labelWidth + 8, labelHeight + 4);
			    shapeRenderer.end();
			    Gdx.gl.glDisable(GL20.GL_BLEND);
			    
			    Label valueLabel = new Label(value);
			    valueLabel.setColor(Color.WHITE);
			    valueLabel.setBounds(new Rectangle(labelX, labelY, labelWidth, labelHeight));
			    valueLabel.render();
		    }
		}
	}

	private void updateStaticUI(GameState state) {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.isVisible()) {
				gameUIelement.setCurrentGameState(GameModel.activeGameState);
				if (gameUIelement.getUpdateAction() != null) gameUIelement.getUpdateAction().execute();
			}
		}
	}

	private void renderStaticUI(GameState state) {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.isVisible()) {
				if (gameUIelement.getElement() != null) {
					gameUIelement.getElement().setBounds(gameUIelement.getBounds());
					gameUIelement.getElement().render();
				}
				if (gameUIelement.getPostRenderAction() != null) gameUIelement.getPostRenderAction().execute();
			}
		}
	}

	public void renderParticles(PerspectiveCamera camera) {
		if (initialized) {
			Planet activeAncientPlanet = null;
			for (Planet planet : GameModel.activeGameState.planets) {
				if (planet.ancient) {
					activeAncientPlanet = planet;
				}
			}
			Player owner = EngineUtility.getPlayer(GameModel.activeGameState.players, activeAncientPlanet.ownerId);
			Player self = EngineUtility.getPlayer(GameModel.activeGameState.players, GameModel.playerId);
			if (activeAncientPlanet != null) {
				if (activeAncientPlanet.ownerId != 0) {
					if (!ParticleRenderer.isStarted("ancient")) ParticleRenderer.start("ancient");
		        } else {
		            ParticleRenderer.stop("ancient");
		        }
			    Vector3 projected = camera.project(new Vector3(activeAncientPlanet.position.x, activeAncientPlanet.position.y, 0));
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
	
	public void setElementVisible(String tag, boolean visible) {
		for (GameUIElement<?> element : elements) {
			if (element.getTag().contentEquals(tag)) {
				element.setVisible(visible);
			}
		}
	}
	
	private void renderSelection(GameState state, PerspectiveCamera camera) {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
		Planet hovered = inputManager.getHoveredPlanet(camera, state);

		shapeRenderer.setProjectionMatrix(camera.combined);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		float baseLineWidth = 2f;
		float density = Gdx.graphics.getDensity();
		Gdx.gl.glLineWidth(baseLineWidth * (density > 1f ? density : 1f));
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.LIGHT_GRAY);

		if (selected != null) {
			shapeRenderer.circle(selected.position.x, selected.position.y, EngineUtility.PLANET_RADIUS + 5);
			Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
			int fleetCost = owner.ships.get(0).cost;
			if (fleetCost > 0 && owner.planetData.maxResourceCapacity > 0) {
				renderResourceCircle(selected.position.x, selected.position.y, fleetCost < selected.currentResources ? Color.GREEN : Color.RED, (float)(fleetCost / owner.planetData.maxResourceCapacity), camera);
			}
		}

		if (hovered != null) {
			boolean isSelectingOrigin = (selected == null && hovered.ownerId == GameModel.playerId);
			boolean isTargeting = (selected != null && hovered.id != selected.id);
			if (isSelectingOrigin || isTargeting) {
				shapeRenderer.circle(hovered.position.x, hovered.position.y, EngineUtility.PLANET_RADIUS + 5);
			}
			if (isTargeting) {
				Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
				Player target = EngineUtility.getPlayer(state.players, hovered.ownerId);
				int fleetCost = owner.ships.get(0).cost;
				if (owner.teamId == target.teamId) {
					renderResourceCircle(hovered.position.x, hovered.position.y, Color.GREEN, (float)(fleetCost / target.planetData.maxResourceCapacity), camera);
				}
			}
		}
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (selected != null && Gdx.input.isTouched()) {
			Vector3 cursorWorld = inputManager.getCurrentTouchWorldPosition(camera);

			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			shapeRenderer.line(selected.position.x, selected.position.y, cursorWorld.x, cursorWorld.y);
			shapeRenderer.end();

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			drawArrow(cursorWorld, selected.position.x, selected.position.y);
			shapeRenderer.end();
		}
		Gdx.gl.glLineWidth(1f);
	}

	private void drawArrow(Vector3 to, float fromX, float fromY) {
		float dx = fromX - to.x;
		float dy = fromY - to.y;
		float angle = (float) Math.atan2(dy, dx);

		float arrowLength = 20f;
		float arrowOffset = 20f;

		float baseX = to.x + (float) Math.cos(angle) * arrowOffset;
		float baseY = to.y + (float) Math.sin(angle) * arrowOffset;

		float leftX = baseX + (float) Math.cos(angle + Math.PI / 6) * arrowLength;
		float leftY = baseY + (float) Math.sin(angle + Math.PI / 6) * arrowLength;

		float rightX = baseX + (float) Math.cos(angle - Math.PI / 6) * arrowLength;
		float rightY = baseY + (float) Math.sin(angle - Math.PI / 6) * arrowLength;

		shapeRenderer.triangle(to.x, to.y, leftX, leftY, rightX, rightY);
	}
	
	private void renderResourceCircle(float x, float y, Color color, float value, PerspectiveCamera camera) {
		complexShapeRenderer.setProjectionMatrix(camera.combined);
		complexShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		complexShapeRenderer.setColor(color);
		complexShapeRenderer.cleanArc(x, y, EngineUtility.PLANET_RADIUS + 12, EngineUtility.PLANET_RADIUS + 20, 90, value * 360f);
		complexShapeRenderer.end();
		
	}
	
	private void renderFleetConnections(GameState state) {
		List<PlanetPair> movements = new ArrayList<>();

		for (Combat combat : state.activeCombats) {
			PlanetPair pair = new PlanetPair(combat.planetIds.get(0), combat.planetIds.get(1));
			movements.add(pair);
		}

		Map<PlanetPair, Color> connectionLines = new HashMap<>();

		for (PlanetPair pair : movements) {
			PlanetPair reverse = new PlanetPair(pair.toId, pair.fromId);
			boolean hasForward = movements.contains(pair);
			boolean hasReverse = movements.contains(reverse);

			if (hasForward && hasReverse) {
				if (pair.fromId > pair.toId) continue;
				connectionLines.put(pair, Color.WHITE);
			} else {
				connectionLines.put(pair, GameConfig.getPlayerColor(EngineUtility.getPlanet(state.planets, pair.fromId).ownerId));
			}
		}

		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		float baseLineWidth = 2f;
		float dynamicLineWidth = baseLineWidth * (Gdx.graphics.getDensity() > 1 ? Gdx.graphics.getDensity() : 1f);
		Gdx.gl.glLineWidth(dynamicLineWidth);
		
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for (Map.Entry<PlanetPair, Color> entry : connectionLines.entrySet()) {
			Planet from = EngineUtility.getPlanet(state.planets, entry.getKey().fromId);
			Planet to = EngineUtility.getPlanet(state.planets, entry.getKey().toId);

			Color faded = new Color(entry.getValue());
			faded.a = 0.25f;

			shapeRenderer.setColor(faded);
			shapeRenderer.line(from.position.x, from.position.y, to.position.x, to.position.y);
		}
		shapeRenderer.end();
		Gdx.gl.glLineWidth(1f);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private void renderMessageText() {
		String message = "Connecting...";
		if (GameModel.activeGameState != null) {
			int winner = GameModel.activeGameState.winner;
			if (winner != 0) {
				if (winner == EngineUtility.getPlayer(GameModel.activeGameState.players, GameModel.playerId).teamId) {
					message = "VICTORY";
				} else if (winner != 0) {
					message = "DEFEATED";
				}
			} else {
				message = "";
			}
		}
		Label messageLabel = new Label(message);
		messageLabel.setColor(Color.WHITE);
		messageLabel.setBounds(new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		messageLabel.render();
	}
	
}
