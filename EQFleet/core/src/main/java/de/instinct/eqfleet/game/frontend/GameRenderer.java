package de.instinct.eqfleet.game.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.combat.Combat;
import de.instinct.engine.combat.Projectile;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.entity.Entity;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.WeaponType;
import de.instinct.engine.util.EngineUtility;
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

public class GameRenderer {

	private PerspectiveCamera camera;
	private GridRenderer gridRenderer;
	
	private Map<Integer, ModelInstance> planetModels;
	private Map<Ship, ModelInstance> shipModels;
	private Map<Projectile, ModelInstance> projectileModels;
	
	private float planetRotationAngle = 0f;
	
	private boolean isFlipped = false;
	
	private final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 4400f);
	
	private GuideEvent currentGuideEvent;
	
	private GameUIRenderer uiRenderer;
	
	public boolean visible;

	public void init() {
		uiRenderer = new GameUIRenderer();
		
		planetModels = new HashMap<>();
		shipModels = new HashMap<>();
		projectileModels = new HashMap<>();
		
		camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(BASE_CAM_POS);
		camera.lookAt(0f, 0f, 0f);
		camera.up.set(0f, 1f, 0f);
		camera.near = 1f;
		camera.far = 5000f;
		camera.update();

		gridRenderer = new GridRenderer();
		
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
					renderPlanets(state);
					renderFleet(state);
					renderProjectiles(state);
				} else {
					renderLoadingScreen(state);
				}
			}
			uiRenderer.render(state, camera);
		}
		
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
		}
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
	            float dx = to.position.x - from.position.x;
	            float dy = to.position.y - from.position.y;
	            float angleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));

	            shipModel.transform.idt();
	            shipModel.transform.translate(new Vector3(ship.position.x, ship.position.y, 0f));
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
	
	private void renderProjectiles(GameState state) {
		for (Combat combat : state.activeCombats) {
			List<Projectile> projectilesToRender = new ArrayList<>(combat.projectiles);
			for (Projectile projectile : projectilesToRender) {
				Entity from = EntityManager.getEntity(state, projectile.id);
				Entity to = EntityManager.getEntity(state, projectile.targetId);
	            
	            float dx = to.position.x - from.position.x;
	            float dy = to.position.y - from.position.y;
	            float angleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));
	            
				ModelInstance projectileModel = projectileModels.get(projectile);
	            if (projectileModel == null) {
	            	projectileModel = ModelLoader.instanciate("projectile");
	            	projectileModel.transform.rotate(Vector3.Z, angleDeg - 90f);
	            	projectileModels.put(projectile, projectileModel);
	            }
	            
	            if (to != null) {
		            projectileModel.transform.idt();
		            projectileModel.transform.translate(new Vector3(projectile.position.x, projectile.position.y, 0f));
		            
		            projectileModel.transform.scale(4f, 4f, 4f);

		            Color color = Color.GRAY;
		            if (projectile.weaponType == WeaponType.LASER) {
		            	color = Color.RED;
		            }
		            if (projectile.weaponType == WeaponType.MISSILE) {
		            	color = Color.BROWN;
		            }
		            for (Material material : projectileModel.materials) {
		                material.set(ColorAttribute.createDiffuse(color));
		            }

		            ModelRenderer.render(camera, projectileModel);
	            }
			}
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