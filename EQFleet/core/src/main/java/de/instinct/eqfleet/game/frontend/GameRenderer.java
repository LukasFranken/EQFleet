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

import de.instinct.engine.combat.Combat;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.ApplicationMode;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.CameraMoveGuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.DialogGuideEvent;
import de.instinct.eqfleet.game.frontend.ui.GameUIRenderer;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.GridRenderer;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeRenderer;

public class GameRenderer {

	private PerspectiveCamera camera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private GameInputManager inputManager;
	private GridRenderer gridRenderer;
	private ComplexShapeRenderer complexShapeRenderer;
	
	private Map<Integer, ModelInstance> planetModels;
	private Map<Ship, ModelInstance> shipModels;
	
	private float planetRotationAngle = 0f;
	
	private boolean isFlipped = false;
	
	private final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 4400f);
	
	private GuideEvent currentGuideEvent;
	
	private GameUIRenderer uiRenderer;
	
	public boolean visible;

	public void init() {
		uiRenderer = new GameUIRenderer();
		complexShapeRenderer = new ComplexShapeRenderer();
		
		planetModels = new HashMap<>();
		shipModels = new HashMap<>();
		
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
					renderFleetConnections(state);
					renderPlanets(state);
					renderFleet(state);
					inputManager.handleInput(camera, state);
					renderSelection(state);
					uiRenderer.render(state);
				} else {
					renderLoadingScreen(state);
				}
			}
		}
		
		renderMessageText();
		renderGuideEvents();
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

	private void renderGuideEvents() {
		processEventPolling();
		if (currentGuideEvent != null) {
			renderEvent();
			processEventTermination();
		}
	}

	private void processEventPolling() {
		if (currentGuideEvent == null && GameModel.guidedEvents != null) {
			GuideEvent peekedGuideEvent = GameModel.guidedEvents.peek();
			if (peekedGuideEvent != null) {
				if (peekedGuideEvent instanceof DialogGuideEvent) {
					DialogGuideEvent currentPeekedDialogGuideEvent = (DialogGuideEvent)peekedGuideEvent;
					if (currentPeekedDialogGuideEvent.getCondition() != null) {
						if (currentPeekedDialogGuideEvent.getCondition().isStartConditionMet()) {
							currentGuideEvent = GameModel.guidedEvents.poll();
						}
					} else {
						currentGuideEvent = GameModel.guidedEvents.poll();
					}
				} else {
					currentGuideEvent = GameModel.guidedEvents.poll();
				}
			}
			
			if (currentGuideEvent != null && currentGuideEvent instanceof DialogGuideEvent) {
				DialogGuideEvent currentDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
				if (currentDialogGuideEvent.getAction() != null) {
					currentDialogGuideEvent.getAction().executeAtStart();
				}
			}
		} else {
			if (GameModel.guidedEvents == null) {
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
		        planetInstance.transform.translate(planet.position.x, planet.position.y, 0f);

		     	Vector3 planetPos = new Vector3(planet.position.x, planet.position.y, 0f);
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
			    instance.transform.translate(planet.position.x, planet.position.y, 0f);

			    //rotate
			    Vector3 planetPos = new Vector3(planet.position.x, planet.position.y, 0f);
			    Vector3 toCamera = new Vector3(camera.position).sub(planetPos).nor();
			    float angle = (float) Math.toDegrees(Math.atan2(toCamera.z, toCamera.y));
			    instance.transform.rotate(Vector3.X, angle + 90);
			    instance.transform.rotate(Vector3.Y, planetRotationAngle);
			    instance.transform.scale(50f, 50f, 50f);

			    ModelRenderer.render(camera, instance);
		    }

		    Vector3 screenPos = camera.project(new Vector3(planet.position.x, planet.position.y, 0f));
		    String value = String.valueOf((int) planet.currentResources);
		    float labelWidth = FontUtil.getFontTextWidthPx(value);
		    float labelHeight = FontUtil.getFontHeightPx();
		    float labelX = screenPos.x - labelWidth / 2f;
		    float labelY = screenPos.y - labelHeight / 2f;
		    
		    Player owner = EngineUtility.getPlayer(state.players, planet.ownerId);
		    renderResourceCircle(planet.position.x, planet.position.y, GameConfig.getPlayerColor(owner.id), (float)(planet.currentResources / owner.planetData.maxResourceCapacity));

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

	private void renderResourceCircle(float x, float y, Color color, float value) {
		complexShapeRenderer.setProjectionMatrix(camera.combined);
		complexShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		complexShapeRenderer.setColor(color);
		complexShapeRenderer.cleanArc(x, y, EngineUtility.PLANET_RADIUS + 12, EngineUtility.PLANET_RADIUS + 20, 90, value * 360f);
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
	
	private void renderFleet(GameState state) {
	    for (Combat combat : state.activeCombats) {
	    	for (Ship ship : combat.ships) {
	    		ModelInstance shipModel = shipModels.get(ship);
	            if (shipModel == null) {
	            	shipModel = ModelLoader.instanciate("ship");
	            	shipModel.transform.scale(15f, 15f, 15f);
	                shipModels.put(ship, shipModel);
	            }

	            Planet from = EngineUtility.getPlanet(state.planets, combat.planetIds.get(0));
	            Planet to = EngineUtility.getPlanet(state.planets, combat.planetIds.get(1));
	            Vector3 pos = new Vector3(from.position.x, from.position.y, 0f);

	            float dx = to.position.x - from.position.x;
	            float dy = to.position.y - from.position.y;
	            float angleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));

	            shipModel.transform.idt();
	            shipModel.transform.translate(pos);
	            shipModel.transform.rotate(Vector3.Z, angleDeg - 90f);
	            shipModel.transform.scale(15f, 15f, 15f);

	            Color color = GameConfig.getPlayerColor(ship.ownerId);
	            for (Material material : shipModel.materials) {
	                material.set(ColorAttribute.createDiffuse(color));
	            }

	            ModelRenderer.render(camera, shipModel);
	    	}
	    }
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

	private void renderSelection(GameState state) {
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
				renderResourceCircle(selected.position.x, selected.position.y, fleetCost < selected.currentResources ? Color.GREEN : Color.RED, (float)(fleetCost / owner.planetData.maxResourceCapacity));
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
					renderResourceCircle(hovered.position.x, hovered.position.y, Color.GREEN, (float)(fleetCost / target.planetData.maxResourceCapacity));
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