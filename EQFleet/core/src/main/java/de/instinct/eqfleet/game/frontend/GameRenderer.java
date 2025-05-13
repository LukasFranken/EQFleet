package de.instinct.eqfleet.game.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.event.GameEvent;
import de.instinct.engine.model.event.types.FleetMovementEvent;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.CameraMoveGuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.DialogGuideEvent;
import de.instinct.eqfleet.game.frontend.ui.UIElementConfig;
import de.instinct.eqfleet.game.frontend.ui.UIRenderer;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.GridRenderer;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;

public class GameRenderer {

	private PerspectiveCamera camera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private GameInputManager inputManager;
	private GridRenderer gridRenderer;
	
	private Map<Integer, ModelInstance> planetModels;
	private Map<FleetMovementEvent, ModelInstance> fleetModels;
	
	private float planetRotationAngle = 0f;
	
	private boolean isFlipped = false;
	
	private final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 4400f);
	
	private GuideEvent currentGuideEvent;
	
	private UIRenderer uiRenderer;
	
	public boolean visible;

	public void init() {
		uiRenderer = new UIRenderer();
		
		planetModels = new HashMap<>();
		fleetModels = new HashMap<>();
		
		camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(BASE_CAM_POS);
		camera.lookAt(0f, 0f, 0f);
		camera.up.set(0f, 1f, 0f);
		camera.near = 1f;
		camera.far = 5000f;
		camera.update();

		shapeRenderer = new ShapeRenderer();
		gridRenderer = new GridRenderer();
		inputManager = new GameInputManager();
		batch = new SpriteBatch();
		
		visible = true;
	}

	public void render() {
		if (visible) {
			GameState state = Game.activeGameState;
			camera.update();

			if (state != null && state.winner == 0) {
				checkFlip();
				gridRenderer.drawGrid(camera);
				uiRenderer.renderParticles(camera);
				renderFleetConnections(state);
				renderPlanets(state);
				renderFleet(state);
				inputManager.handleInput(camera, state);
				renderSelection(state);
				uiRenderer.render(state);
			}
		}
		
		renderMessageText();
		renderGuideEvents();
	}

	private void checkFlip() {
		Player self = EngineUtility.getPlayer(Game.activeGameState, Game.playerId);
		if (isFlipped && self.teamId == 1) {
			flip();
		}
		if (!isFlipped && self.teamId == 2) {
			flip();
		}
	}

	private void renderGuideEvents() {
		processEventPolling();
		if (currentGuideEvent != null) {
			renderEvent();
			processEventTermination();
		}
	}

	private void processEventPolling() {
		if (currentGuideEvent == null && Game.guidedEvents != null) {
			GuideEvent peekedGuideEvent = Game.guidedEvents.peek();
			if (peekedGuideEvent != null) {
				if (peekedGuideEvent instanceof DialogGuideEvent) {
					DialogGuideEvent currentPeekedDialogGuideEvent = (DialogGuideEvent)peekedGuideEvent;
					if (currentPeekedDialogGuideEvent.getCondition() != null) {
						if (currentPeekedDialogGuideEvent.getCondition().isStartConditionMet()) {
							currentGuideEvent = Game.guidedEvents.poll();
						}
					} else {
						currentGuideEvent = Game.guidedEvents.poll();
					}
				} else {
					currentGuideEvent = Game.guidedEvents.poll();
				}
			}
			
			if (currentGuideEvent != null && currentGuideEvent instanceof DialogGuideEvent) {
				DialogGuideEvent currentDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
				if (currentDialogGuideEvent.getAction() != null) {
					currentDialogGuideEvent.getAction().executeAtStart();
				}
			}
		} else {
			if (Game.guidedEvents == null) {
				currentGuideEvent = null;
			}
		}
	}
	
	private void renderEvent() {
		if (currentGuideEvent instanceof CameraMoveGuideEvent) {
		    CameraMoveGuideEvent camEvent = (CameraMoveGuideEvent) currentGuideEvent;
		    
		    if (camEvent.getStartCameraPos() == null) {
		        camEvent.setStartCameraPos(new Vector3(camera.position));
		    }
		    
		    float ratio = camEvent.getElapsed() / camEvent.getDuration();
		    
		    Vector3 startPos = camEvent.getStartCameraPos();
		    Vector3 targetPos = camEvent.getTargetCameraPos();
		    float newX = MathUtil.easeInOut(startPos.x, targetPos.x, ratio);
		    float newY = MathUtil.easeInOut(startPos.y, targetPos.y, ratio);
		    float newZ = MathUtil.easeInOut(startPos.z, targetPos.z, ratio);
		    
		    camera.position.set(newX, newY, newZ);
		    camera.update();
		}
		if (currentGuideEvent instanceof DialogGuideEvent) {
			DialogGuideEvent currentDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
			if (currentDialogGuideEvent.getMessage() != null) {
				Label messageLabel = new Label(currentDialogGuideEvent.getMessage().getText());
				switch (currentDialogGuideEvent.getMessage().getVerticalAlignment()) {
				case TOP:
					messageLabel.setBounds(new Rectangle(20, Gdx.graphics.getHeight() - 150, Gdx.graphics.getWidth() - 40, 70));
					break;
				case CENTER:
					messageLabel.setBounds(new Rectangle(20, (Gdx.graphics.getHeight() / 2) - 35, Gdx.graphics.getWidth() - 40, 70));
					break;
				case BOTTOM:
					messageLabel.setBounds(new Rectangle(20, 20, Gdx.graphics.getWidth() - 40, 70));
					break;
					
				}
				messageLabel.setBackgroundColor(Color.BLACK);
				Border labelBorder = new Border();
				labelBorder.setColor(DefaultUIValues.skinColor);
				labelBorder.setSize(2);
				messageLabel.setBorder(labelBorder);
				messageLabel.render();
			}
			
		}
	}
	
	private void processEventTermination() {
		boolean setToTerminate = false;
		currentGuideEvent.setElapsed(currentGuideEvent.getElapsed() + Gdx.graphics.getDeltaTime());
		if (currentGuideEvent instanceof DialogGuideEvent) {
			DialogGuideEvent currentPeekedDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
			if (currentPeekedDialogGuideEvent.getCondition() != null) {
				if (currentPeekedDialogGuideEvent.getCondition().isEndConditionMet()) {
					setToTerminate = true;
				}
			} else {
				if (currentGuideEvent.getElapsed() > currentGuideEvent.getDuration()) {
					setToTerminate = true;
				}
			}
		} else {
			if (currentGuideEvent.getElapsed() > currentGuideEvent.getDuration()) {
				setToTerminate = true;
			}
		}
		
		if (setToTerminate) {
			if (currentGuideEvent instanceof DialogGuideEvent) {
				DialogGuideEvent currentPeekedDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
				if  (currentPeekedDialogGuideEvent.getAction() != null) {
					currentPeekedDialogGuideEvent.getAction().executeAtEnd();
				}
			}
			currentGuideEvent = null;
		}
	}

	private void renderPlanets(GameState state) {
		if (planetModels.isEmpty() && state != null) {
		    for (Planet planet : state.planets) {
		        ModelInstance planetInstance = ModelLoader.instanciate("planet");
		        planetInstance.transform.idt();
		        planetInstance.transform.translate(planet.xPos, planet.yPos, 0f);

		     	Vector3 planetPos = new Vector3(planet.xPos, planet.yPos, 0f);
		     	Vector3 toCamera = new Vector3(camera.position).sub(planetPos).nor();
		     	float angle = (float) Math.toDegrees(Math.atan2(toCamera.z, toCamera.y));

		     	planetInstance.transform.rotate(Vector3.X, angle + 90);
		     	planetInstance.transform.scale(40f, 40f, 40f);

		        planetModels.put(planet.id, planetInstance);
		    }
		}
		planetRotationAngle += Gdx.graphics.getDeltaTime() * 10f;
		for (Planet planet : state.planets) {
		    ModelInstance instance = planetModels.get(planet.id);
		    if (instance != null) {
		    	instance.materials.get(1).set(ColorAttribute.createDiffuse(Color.BLACK));
		    	instance.materials.get(0).set(ColorAttribute.createDiffuse(planet.ancient && planet.ownerId == 0 ? GameConfig.ancientColor : GameConfig.getPlayerColor(planet.ownerId)));
		    	
		    	instance.transform.idt();
			    instance.transform.translate(planet.xPos, planet.yPos, 0f);

			    //rotate
			    Vector3 planetPos = new Vector3(planet.xPos, planet.yPos, 0f);
			    Vector3 toCamera = new Vector3(camera.position).sub(planetPos).nor();
			    float angle = (float) Math.toDegrees(Math.atan2(toCamera.z, toCamera.y));
			    instance.transform.rotate(Vector3.X, angle + 90);
			    instance.transform.rotate(Vector3.Y, planetRotationAngle);
			    instance.transform.scale(50f, 50f, 50f);

			    ModelRenderer.render(camera, instance);
		    }

		    Vector3 screenPos = camera.project(new Vector3(planet.xPos, planet.yPos, 0f));
		    String valueLabel = String.valueOf((int) planet.value);
		    float labelWidth = FontUtil.getFontTextWidthPx(valueLabel);
		    float labelHeight = FontUtil.getFontHeightPx();
		    float labelX = screenPos.x - labelWidth / 2f;
		    float labelY = screenPos.y - labelHeight / 2f;

		    shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		    Gdx.gl.glEnable(GL20.GL_BLEND);
		    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		    shapeRenderer.setColor(0f, 0f, 0f, 0.7f);
		    shapeRenderer.rect(labelX - 4, labelY - 2, labelWidth + 8, labelHeight + 4);
		    shapeRenderer.end();
		    Gdx.gl.glDisable(GL20.GL_BLEND);

		    FontUtil.draw(Color.WHITE, valueLabel, labelX, labelY + labelHeight / 2f + (FontUtil.getFontHeightPx() / 2));
		}
	}

	private void renderMessageText() {
		String message = "Connecting...";
		if (Game.activeGameState != null) {
			int winner = Game.activeGameState.winner;
			if (winner != 0) {
				if (winner == EngineUtility.getPlayer(Game.activeGameState, Game.playerId).teamId) {
					message = "VICTORY";
				} else if (winner != 0) {
					message = "DEFEATED";
				}
			} else {
				message = "";
			}
		}
	    FontUtil.draw(Color.WHITE, message,
	        (Gdx.graphics.getWidth() / 2) - (FontUtil.getFontTextWidthPx(message) / 2),
	        Gdx.graphics.getHeight() / 2);
	}
	
	private void renderFleet(GameState state) {
	    fleetModels.keySet().retainAll(state.activeEvents);

	    for (GameEvent event : state.activeEvents) {
	        if (event instanceof FleetMovementEvent) {
	            FleetMovementEvent fleet = (FleetMovementEvent) event;

	            ModelInstance ship = fleetModels.get(fleet);
	            if (ship == null) {
	                ship = ModelLoader.instanciate("ship");
	                ship.transform.scale(15f, 15f, 15f);
	                fleetModels.put(fleet, ship);
	            }

	            Planet from = EngineUtility.getPlanet(state, fleet.fromPlanetId);
	            Planet to = EngineUtility.getPlanet(state, fleet.toPlanetId);
	            Vector3 pos = EngineUtility.getFleetWorldPosition(state, fleet);

	            float dx = to.xPos - from.xPos;
	            float dy = to.yPos - from.yPos;
	            float angleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));

	            ship.transform.idt();
	            ship.transform.translate(pos);
	            ship.transform.rotate(Vector3.Z, angleDeg - 90f);
	            ship.transform.scale(15f, 15f, 15f);

	            Color color = GameConfig.getPlayerColor(fleet.playerId);
	            for (Material material : ship.materials) {
	                material.set(ColorAttribute.createDiffuse(color));
	            }

	            ModelRenderer.render(camera, ship);
	            
	            Vector3 screenPos = camera.project(new Vector3(pos));
	            FontUtil.draw(Color.WHITE, String.valueOf(fleet.value), screenPos.x + 10, screenPos.y + (FontUtil.getFontHeightPx() / 2));
	        }
	    }
	}

	private void renderFleetConnections(GameState state) {
		List<PlanetPair> movements = new ArrayList<>();

		for (GameEvent event : state.activeEvents) {
			if (event instanceof FleetMovementEvent) {
				FleetMovementEvent fleet = (FleetMovementEvent) event;
				PlanetPair pair = new PlanetPair(fleet.fromPlanetId, fleet.toPlanetId);
				movements.add(pair);
			}
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
				connectionLines.put(pair, GameConfig.getPlayerColor(EngineUtility.getPlanet(state, pair.fromId).ownerId));
			}
		}

		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		float baseLineWidth = 2f;
		float dynamicLineWidth = baseLineWidth * (Gdx.graphics.getDensity() > 1 ? Gdx.graphics.getDensity() : 1f);
		Gdx.gl.glLineWidth(dynamicLineWidth);
		
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for (Map.Entry<PlanetPair, Color> entry : connectionLines.entrySet()) {
			Planet from = EngineUtility.getPlanet(state, entry.getKey().fromId);
			Planet to = EngineUtility.getPlanet(state, entry.getKey().toId);

			Color faded = new Color(entry.getValue());
			faded.a = 0.25f;

			shapeRenderer.setColor(faded);
			shapeRenderer.line(from.xPos, from.yPos, to.xPos, to.yPos);
		}
		shapeRenderer.end();
		Gdx.gl.glLineWidth(1f);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private void renderSelection(GameState state) {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state, selectedId) : null;
		Planet hovered = inputManager.getHoveredPlanet(camera, state);

		shapeRenderer.setProjectionMatrix(camera.combined);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		float baseLineWidth = 2f;
		float density = Gdx.graphics.getDensity();
		Gdx.gl.glLineWidth(baseLineWidth * (density > 1f ? density : 1f));
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.LIGHT_GRAY);

		if (selected != null)
			shapeRenderer.circle(selected.xPos, selected.yPos, EngineUtility.PLANET_RADIUS + 5);

		if (hovered != null) {
			boolean isSelectingOrigin = (selected == null && hovered.ownerId == Game.playerId);
			boolean isTargeting = (selected != null && hovered.id != selected.id);
			if (isSelectingOrigin || isTargeting) {
				shapeRenderer.circle(hovered.xPos, hovered.yPos, EngineUtility.PLANET_RADIUS + 5);
			}
		}
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (selected != null && Gdx.input.isTouched()) {
			Vector3 cursorWorld = inputManager.getCurrentTouchWorldPosition(camera);

			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			shapeRenderer.line(selected.xPos, selected.yPos, cursorWorld.x, cursorWorld.y);
			shapeRenderer.end();

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			drawArrow(cursorWorld, selected.xPos, selected.yPos);
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

	public void flip() {
		camera.rotate(180f, 0f, 0f, 1f);
		isFlipped = !isFlipped;
	}

	public UIElementConfig getUIElementConfig() {
		return uiRenderer.getElementConfig();
	}
	
}