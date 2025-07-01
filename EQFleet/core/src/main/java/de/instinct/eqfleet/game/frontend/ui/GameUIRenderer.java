package de.instinct.eqfleet.game.frontend.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.core.API;
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
import de.instinct.eqfleet.game.frontend.GameRenderer;
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
	private GameInputManager inputManager;
	private DefenseUIRenderer defenseUIRenderer;
	
	private PerspectiveCamera camera;
	private GameState state;
	
	public GameUIRenderer() {
		uiLoader = new GameUILoader();
		ParticleRenderer.loadParticles("ancient", "ancient");
		ParticleRenderer.stop("ancient");
		shapeRenderer = new ShapeRenderer();
		complexShapeRenderer = new ComplexShapeRenderer();
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
	
	public void render() {
		if (GameModel.activeGameState != null) {
			if (initialized && state.winner == 0) {
				inputManager.handleInput(camera, state);
				updateStaticUI();
				renderStaticUI();
				renderResourceCircles();
				defenseUIRenderer.render(state, camera);
				renderShipSelection();
			}
		}
		renderMessageText();
	}

	private void renderResourceCircles() {
		for (Planet planet : state.planets) {
			Vector3 screenPos = camera.project(new Vector3(planet.position.x, planet.position.y, 0f));
		    String value = String.valueOf((int) planet.currentResources);
		    float labelWidth = FontUtil.getFontTextWidthPx(value);
		    float labelHeight = FontUtil.getFontHeightPx();
		    float labelX = screenPos.x - labelWidth / 2f;
		    float labelY = screenPos.y - labelHeight / 2f;
		    
		    Player owner = EngineUtility.getPlayer(state.players, planet.ownerId);
		    renderResourceCircle(planet.position.x, planet.position.y, GameConfig.getPlayerColor(owner.id), (float)(planet.currentResources / owner.planetData.maxResourceCapacity));

		    if (GlobalStaticData.mode == ApplicationMode.DEV) {
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

	private void updateStaticUI() {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.isVisible()) {
				gameUIelement.setCurrentGameState(GameModel.activeGameState);
				if (gameUIelement.getUpdateAction() != null) gameUIelement.getUpdateAction().execute();
			}
		}
	}

	private void renderStaticUI() {
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

	public void renderParticles() {
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
	
	private void renderShipSelection() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		setDensityLineWidth();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.LIGHT_GRAY);
		
		renderSelected();
		renderHovered();
		renderArrow();
		
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	private void renderSelected() {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
		
		if (selected != null) {
			shapeRenderer.circle(selected.position.x, selected.position.y, EngineUtility.PLANET_RADIUS);
			Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
			
			Integer shipIndex = inputManager.getSelectedShipId();
			if (owner.ships.size() == 1) shipIndex = 0;
			if (shipIndex != null) {
				int fleetCost = owner.ships.get(shipIndex).cost;
				if (fleetCost > 0 && owner.planetData.maxResourceCapacity > 0) {
					renderResourceCircle(selected.position.x, selected.position.y, fleetCost < selected.currentResources ? Color.GREEN : Color.RED, (float)(fleetCost / owner.planetData.maxResourceCapacity));
				}
			}
			if (owner.ships.size() > 1 && inputManager.getSelectedShipId() == null) {
				renderShipSelectionCircle(selected.position.x, selected.position.y, owner, inputManager.getSelectedShipId());
			}
		}
	}

	private void setDensityLineWidth() {
		float baseLineWidth = 2f;
		float density = Gdx.graphics.getDensity();
		Gdx.gl.glLineWidth(baseLineWidth * (density > 1f ? density : 1f));
	}

	private void renderHovered() {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
		Planet hovered = inputManager.getHoveredPlanet(camera, state);
		
		if (hovered != null) {
			boolean isSelectingOrigin = (selected == null && hovered.ownerId == GameModel.playerId);
			boolean isTargeting = (selected != null && hovered.id != selected.id);
			if (isSelectingOrigin || isTargeting) {
				shapeRenderer.circle(hovered.position.x, hovered.position.y, EngineUtility.PLANET_RADIUS);
			}
			if (isTargeting) {
				Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
				Player target = EngineUtility.getPlayer(state.players, hovered.ownerId);
				int fleetCost = owner.ships.get(0).cost;
				if (owner.teamId == target.teamId) {
					renderResourceCircle(hovered.position.x, hovered.position.y, Color.GREEN, (float)(fleetCost / target.planetData.maxResourceCapacity));
				}
			}
			if (hovered.weapon != null) {
				shapeRenderer.end();
				setDensityLineWidth();
				shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
				shapeRenderer.setColor(GameConfig.getPlayerColor(hovered.ownerId));
				shapeRenderer.circle(hovered.position.x, hovered.position.y, hovered.weapon.range + EngineUtility.PLANET_RADIUS);
			}
		}
	}

	private void renderArrow() {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
		if (selected != null && Gdx.input.isTouched()) {
			Vector3 cursorWorld = inputManager.getCurrentTouchWorldPosition(camera);
			
			shapeRenderer.end();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			shapeRenderer.line(selected.position.x, selected.position.y, cursorWorld.x, cursorWorld.y);
			
			shapeRenderer.end();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			drawArrow(cursorWorld, selected.position.x, selected.position.y);
			shapeRenderer.end();
			Gdx.gl.glLineWidth(1f);
		}
	}

	private void renderShipSelectionCircle(float x, float y, Player owner, Integer selectedShipId) {
	    int shipCount = owner.ships.size();
	    float outerRadius = EngineUtility.PLANET_RADIUS + 30f;
	    float innerRadius = EngineUtility.PLANET_RADIUS + 10f;
	    float sectionAngle = 360f / shipCount;
	    
	    shapeRenderer.end();
	    Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	    
	    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	    
	    // Draw selection circle divided into sections
	    for (int i = 0; i < shipCount; i++) {
	        // Highlight selected ship
	        boolean isSelected = (selectedShipId != null && selectedShipId == i);
	        if (isSelected) {
	            shapeRenderer.setColor(1f, 1f, 0.5f, 0.7f); // Highlight color
	        } else {
	            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f); // Normal color
	        }
	        
	        float startAngle = i * sectionAngle;
	        float endAngle = (i + 1) * sectionAngle;
	        
	        // Draw wedge for this ship type
	        for (float angle = startAngle; angle < endAngle; angle += 1f) {
	            float rad1 = (float) Math.toRadians(angle);
	            float rad2 = (float) Math.toRadians(angle + 1f);
	            
	            float x1 = x + innerRadius * (float) Math.cos(rad1);
	            float y1 = y + innerRadius * (float) Math.sin(rad1);
	            float x2 = x + outerRadius * (float) Math.cos(rad1);
	            float y2 = y + outerRadius * (float) Math.sin(rad1);
	            float x3 = x + outerRadius * (float) Math.cos(rad2);
	            float y3 = y + outerRadius * (float) Math.sin(rad2);
	            float x4 = x + innerRadius * (float) Math.cos(rad2);
	            float y4 = y + innerRadius * (float) Math.sin(rad2);
	            
	            shapeRenderer.triangle(x1, y1, x2, y2, x3, y3);
	            shapeRenderer.triangle(x1, y1, x3, y3, x4, y4);
	        }
	        
	        // Draw ship type indicator (could be improved with ship icons later)
	        float midAngle = (float) Math.toRadians((startAngle + endAngle) / 2);
	        float labelX = x + (innerRadius + outerRadius) / 2 * (float) Math.cos(midAngle);
	        float labelY = y + (innerRadius + outerRadius) / 2 * (float) Math.sin(midAngle);
	        
	        shapeRenderer.setColor(Color.WHITE);
	        shapeRenderer.circle(labelX, labelY, 5f);
	    }
	    
	    shapeRenderer.end();
	    Gdx.gl.glDisable(GL20.GL_BLEND);
	    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
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
	
	private void renderResourceCircle(float x, float y, Color color, float value) {
		float radius = EngineUtility.PLANET_RADIUS + 5f;
		float thickness = 8f;
		complexShapeRenderer.setProjectionMatrix(camera.combined);
		complexShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		complexShapeRenderer.setColor(color);
		complexShapeRenderer.cleanArc(x, y, radius, radius + thickness, 90 + (GameRenderer.isFlipped ? 180 : 0), value * 360f);
		complexShapeRenderer.end();
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

	public void setCamera(PerspectiveCamera camera) {
		this.camera = camera;
	}

	public void setState(GameState state) {
		this.state = state;
	}
	
}
