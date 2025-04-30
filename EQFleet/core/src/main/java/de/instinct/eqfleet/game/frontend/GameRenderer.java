package de.instinct.eqfleet.game.frontend;

import java.util.HashMap;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.event.GameEvent;
import de.instinct.engine.model.event.types.FleetMovementEvent;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.CameraMoveGuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.DialogGuideEvent;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.GridRenderer;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class GameRenderer {

	private PerspectiveCamera camera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private GameInputManager inputManager;
	private GridRenderer gridRenderer;
	
	private Map<Integer, ModelInstance> planetModels;
	private Map<FleetMovementEvent, ModelInstance> fleetModels;

	private final Color[] ownerColors = { Color.DARK_GRAY, Color.GOLD, Color.FOREST, Color.RED, Color.CHARTREUSE, Color.ORANGE };
	
	private float planetRotationAngle = 0f;
	
	private BoxedRectangularLoadingBar cpLoadingBar;
	private BoxedRectangularLoadingBar enemyCPLoadingBar;
	private PlainRectangularLoadingBar atpLoadingBar;
	private PlainRectangularLoadingBar enemyATPLoadingBar;
	
	private boolean isFlipped = false;
	
	private final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 4400f);
	
	private GuideEvent currentGuideEvent;
	
	private GameRendererConfig config;
	
	private int ancientOwner = 0;
	private Planet activeAncientPlanet;
	
	private float AP_GLOWUP_DELAY = 2.8f;
	
	private float ownGlowAlpha;
	private float enemyGlowAlpha;
	
	private float ownAlphaStore;
	private float enemyAlphaStore;
	private float ownElapsed;
	private float enemyElapsed;

	public void init() {
		config = GameRendererConfig.builder()
				.visible(true)
				.uiElementConfig(UIElementConfig.builder()
						.isOwnCPVisible(true)
						.isEnemyCPVisible(true)
						.isOwnAPVisible(true)
						.isEnemyAPVisible(true)
						.isTimeVisible(true)
						.build())
				.build();
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
		
		cpLoadingBar = new BoxedRectangularLoadingBar();
		cpLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		enemyCPLoadingBar = new BoxedRectangularLoadingBar();
		enemyCPLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		atpLoadingBar = new PlainRectangularLoadingBar();
		atpLoadingBar.setBar(TextureManager.createTexture(Color.GOLD));
		atpLoadingBar.setCustomDescriptor("");
		atpLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		enemyATPLoadingBar = new PlainRectangularLoadingBar();
		enemyATPLoadingBar.setBar(TextureManager.createTexture(Color.GOLD));
		enemyATPLoadingBar.setCustomDescriptor("");
		enemyATPLoadingBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		ParticleRenderer.loadParticles("ancient", "ancient");
		ParticleRenderer.stop("ancient");
		
		ownGlowAlpha = 0f;
		enemyGlowAlpha = 0f;
		ownAlphaStore = 0f;
		enemyAlphaStore = 0f;
		ownElapsed = 0f;
		enemyElapsed = 0f;
	}
	
	public GameRendererConfig getConfig()  {
		return config;
	}

	public void render() {
		GameState state = Game.activeGameState;
		
		if (isFlipped && Game.playerId == 1) {
			flip();
		}
		if (!isFlipped && Game.playerId == 2) {
			flip();
		}
		
		camera.update();

		if (state != null && state.winner == 0) {
			if (config.isVisible()) {
				gridRenderer.drawGrid(camera);
				renderParticles();
				renderFleetConnections(state);
				renderPlanets(state);
				renderFleet(state);
				inputManager.handleInput(camera, state);
				renderSelection(state);
				renderUI(state);
				renderUIOverlay();
			}
		} else {
			activeAncientPlanet = null;
		}
		
		renderMessageText();
		renderGuideEvents();
	}

	private void renderParticles() {
		for (Planet planet : Game.activeGameState.planets) {
			if (planet.ancient) {
				activeAncientPlanet = planet;
			}
		}
		
		if (activeAncientPlanet != null) {
		    if (ancientOwner != activeAncientPlanet.ownerId) {
		        if (activeAncientPlanet.ownerId != 0) {
		            ParticleRenderer.start("ancient");
		        } else {
		            ParticleRenderer.stop("ancient");
		        }
		        ancientOwner = activeAncientPlanet.ownerId;
		        if (ancientOwner == 1) {
		        	ownElapsed = 0f;
		        }
		        if (ancientOwner == 2) {
		        	enemyElapsed = 0f;
		        }
		    }

		    Vector3 projected = camera.project(new Vector3(activeAncientPlanet.xPos, activeAncientPlanet.yPos, 0));
		    Vector2 source = new Vector2(projected.x, projected.y);
		    Vector2 target = (activeAncientPlanet.ownerId == Game.playerId)
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
	
	private void renderUIOverlay() {
	    Rectangle overlayBounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    ownElapsed += Gdx.graphics.getDeltaTime();
	    enemyElapsed += Gdx.graphics.getDeltaTime();
	    if (activeAncientPlanet != null) {
	    	if (activeAncientPlanet.ownerId != 0) {
	    		if (activeAncientPlanet.ownerId == Game.playerId) {
	    			ownAlphaStore += Gdx.graphics.getDeltaTime();
	    		} else {
	    			enemyAlphaStore += Gdx.graphics.getDeltaTime();
	    		}
	    	}
    	}
	    float conversion = Gdx.graphics.getDeltaTime();
	    if (ownElapsed > AP_GLOWUP_DELAY) {
	    	if (ownAlphaStore > 0) {
	    		ownGlowAlpha = MathUtil.clamp(0f, 1f, ownGlowAlpha + conversion);
	    		ownAlphaStore -= conversion;
	    	} else {
	    		ownGlowAlpha = MathUtil.clamp(0f, 1f, ownGlowAlpha - conversion);
	    	}
	    }
	    if (enemyElapsed > AP_GLOWUP_DELAY) {
	    	if (enemyAlphaStore > 0) {
	    		enemyGlowAlpha = MathUtil.clamp(0f, 1f, enemyGlowAlpha + conversion);
	    		enemyAlphaStore -= conversion;
	    	} else {
	    		enemyGlowAlpha = MathUtil.clamp(0f, 1f, enemyGlowAlpha - conversion);
	    	}
	    }
		

	    if (config.getUiElementConfig().isOwnCPVisible())
	        TextureManager.draw(TextureManager.getTexture("ui/image", "game_ui_layout_own_cp"), overlayBounds);
	    if (config.getUiElementConfig().isEnemyCPVisible())
	        TextureManager.draw(TextureManager.getTexture("ui/image", "game_ui_layout_enemy_cp"), overlayBounds);
	    if (config.getUiElementConfig().isOwnAPVisible())
	        TextureManager.draw(TextureManager.getTexture("ui/image", "game_ui_layout_own_ap"), overlayBounds);
	    if (config.getUiElementConfig().isEnemyAPVisible()) {
	    	TextureManager.draw(TextureManager.getTexture("ui/image", "game_ui_layout_enemy_ap"), overlayBounds);
	    	TextureManager.draw(TextureManager.getTexture("ui/image", "game_ui_layout_own_ap_glow"), overlayBounds, ownGlowAlpha);
	    }
	    if (config.getUiElementConfig().isTimeVisible()) {
	    	TextureManager.draw(TextureManager.getTexture("ui/image", "game_ui_layout_time"), overlayBounds);
	    	TextureManager.draw(TextureManager.getTexture("ui/image", "game_ui_layout_enemy_ap_glow"), overlayBounds, enemyGlowAlpha);
	    }
	}

	private void renderUI(GameState state) {
		int enemyId = Game.playerId == 1 ? 2 : 1;
		Player self = EngineUtility.getPlayer(state, Game.playerId);
		Player enemy = EngineUtility.getPlayer(state, enemyId);

		float scaleX = Gdx.graphics.getWidth() / 400f;
		float scaleY = Gdx.graphics.getHeight() / 900f;

		long remainingMS = state.maxGameTimeMS - state.gameTimeMS;
		String remainingTimeLabel = StringUtils.generateCountdownLabel(remainingMS, false);
		if (config.getUiElementConfig().isTimeVisible()) FontUtil.draw(remainingMS < 60_000 ? Color.RED : Color.WHITE, remainingTimeLabel, 291 * scaleX, 850 * scaleY);

		cpLoadingBar.setBounds(new Rectangle(
			51 * scaleX, 
			18 * scaleY, 
			330 * scaleX, 
			25 * scaleY));
		cpLoadingBar.setMaxValue(self.maxCommandPoints);
		cpLoadingBar.setCurrentValue(self.currentCommandPoints);
		if (config.getUiElementConfig().isOwnCPVisible()) cpLoadingBar.render();

		enemyCPLoadingBar.setBounds(new Rectangle(
			51 * scaleX, 
			831 * scaleY, 
			82 * scaleX, 
			26 * scaleY));
		enemyCPLoadingBar.setMaxValue(enemy.maxCommandPoints);
		enemyCPLoadingBar.setCurrentValue(enemy.currentCommandPoints);
		if (config.getUiElementConfig().isEnemyCPVisible()) enemyCPLoadingBar.render();

		atpLoadingBar.setBounds(new Rectangle(
			20 * scaleX, 
			75 * scaleY, 
			27 * scaleX, 
			207 * scaleY));
		atpLoadingBar.setDirection(Direction.NORTH);
		atpLoadingBar.setMaxValue(state.atpToWin);
		atpLoadingBar.setCurrentValue(self.ancientTechnologyPoints);
		atpLoadingBar.setCustomDescriptor("");
		if (config.getUiElementConfig().isOwnAPVisible()) atpLoadingBar.render();

		enemyATPLoadingBar.setBounds(new Rectangle(
			20 * scaleX,
			593 * scaleY, 
			27 * scaleX, 
			207 * scaleY));
		enemyATPLoadingBar.setDirection(Direction.SOUTH);
		enemyATPLoadingBar.setMaxValue(state.atpToWin);
		enemyATPLoadingBar.setCurrentValue(enemy.ancientTechnologyPoints);
		if (config.getUiElementConfig().isEnemyAPVisible()) enemyATPLoadingBar.render();
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
		    	instance.materials.get(0).set(ColorAttribute.createDiffuse(getOwnerColor(planet.ownerId, planet.ancient)));
		    	
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
				if (winner == Game.playerId) {
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

	            Color color = getOwnerColor(fleet.playerId, false);
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
		Map<PlanetPair, Integer> movementMap = new HashMap<>();

		for (GameEvent event : state.activeEvents) {
			if (event instanceof FleetMovementEvent) {
				FleetMovementEvent fleet = (FleetMovementEvent) event;
				PlanetPair pair = new PlanetPair(fleet.fromPlanetId, fleet.toPlanetId);
				int flag = (fleet.playerId == Game.playerId) ? 1 : 2;
				movementMap.put(pair, flag);
			}
		}

		Map<PlanetPair, Color> connectionLines = new HashMap<>();

		for (PlanetPair pair : movementMap.keySet()) {
			PlanetPair reverse = new PlanetPair(pair.toId, pair.fromId);
			boolean hasForward = movementMap.containsKey(pair);
			boolean hasReverse = movementMap.containsKey(reverse);

			if (hasForward && hasReverse) {
				if (pair.fromId > pair.toId) continue;
				connectionLines.put(pair, Color.BLUE);
			} else {
				int flag = movementMap.get(pair);
				connectionLines.put(pair, (flag == 1) ? ownerColors[2] : ownerColors[3]);
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

	private Color getOwnerColor(int ownerId, boolean ancient) {
		if (ownerId == 0 & ancient) return ownerColors[1];
		if (ownerId == 0 & !ancient) return ownerColors[0];
		if (ownerId != Game.playerId && ancient) return ownerColors[5];
		if (ownerId == Game.playerId && ancient) return ownerColors[4];
		if (ownerId != Game.playerId) return ownerColors[3];
		if (ownerId == Game.playerId) return ownerColors[2];
		return ownerColors[0];
	}

	public void flip() {
		camera.rotate(180f, 0f, 0f, 1f);
		isFlipped = !isFlipped;
	}
	
}