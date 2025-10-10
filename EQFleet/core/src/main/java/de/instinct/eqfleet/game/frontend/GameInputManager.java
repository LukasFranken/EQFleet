package de.instinct.eqfleet.game.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.net.message.types.BuildTurretMessage;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.net.message.types.GamePauseMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;

public class GameInputManager {
	
	private float HITBOX_INCREASE = 50f;
	
	private Integer selectedPlanetId = null;
	private Integer hoveredShipId = null;
    private Integer selectedShipId = null;
    private Integer hoveredBuildingId = null;
    
    private Vector3 dragStartPosition = new Vector3();
    private boolean isDragging = false;
    private float radialSelectionThreshold = 80f;
    private float radialHoverThreshold = 20f;
    
    public void handleInput(PerspectiveCamera camera, GameState state) {
        if (!GameModel.inputEnabled) {
            return;
        }
        if (state.winner == 0 && state.resumeCountdownMS <= 0 && state.teamPause == 0) {
        	Vector3 worldTouch = getTouchWorldPosition(camera);
        	if (Gdx.input.justTouched()) {
                for (Planet planet : state.planets) {
                    if (planet.ownerId == GameModel.playerId && isTouchInsidePlanet(worldTouch, planet)) {
                        selectedPlanetId = planet.id;
                        dragStartPosition.set(worldTouch);
                        isDragging = true;
                        selectedShipId = null;
                        break;
                    }
                }
                
                Rectangle timerBounds = GraphicsUtil.scaleFactorAdjusted(new Rectangle(320, 820, 80, 50));
                if (state.teamPause == 0 && timerBounds.contains(InputUtil.getMousePosition())) {
                	GamePauseMessage order = new GamePauseMessage();
                	order.gameUUID = state.gameUUID;
                	order.userUUID = API.authKey;
                	order.reason = "Manual Pause";
                	order.pause = true;
                	GameModel.outputMessageQueue.add(order);
                }
            }
            
            if (Gdx.input.isTouched() && selectedPlanetId != null && isDragging) {
                Player player = getPlayerForSelectedPlanet(state);
                switch (GameModel.mode) {
				case UNIT_CONTROL:
					if (selectedShipId == null) {
                		if (player.ships.size() > 1) {
                    		updateShipSelectionFromDrag(worldTouch, state);
                    	} else {
                    		selectedShipId = 0;
                    	}
                	}
					break;
				case CONSTRUCTION:
					updateBuildingSelectionFromDrag(worldTouch, state);
					break;
				case Q_LINK:
					//updateBuildingSelectionFromDrag(worldTouch, state);
					break;
				}
            }

            if (!Gdx.input.isTouched() && selectedPlanetId != null) {
                isDragging = false;
                for (Planet planet : state.planets) {
                    if (isTouchInsidePlanet(worldTouch, planet)) {
                    	if (planet.id != selectedPlanetId) {
                    		FleetMovementMessage order = new FleetMovementMessage();
                            order.gameUUID = state.gameUUID;
                            order.userUUID = API.authKey;
                            order.fromPlanetId = selectedPlanetId;
                            order.toPlanetId = planet.id;
                            order.shipId = selectedShipId == null ? 0 : selectedShipId;
                            GameModel.outputMessageQueue.add(order);
                            break;
                    	}
                    }
                }
                selectedPlanetId = null;
                selectedShipId = null;
            }
        }
    }
	
	private void updateShipSelectionFromDrag(Vector3 currentPosition, GameState state) {
        Planet sourcePlanet = EngineUtility.getPlanet(state.planets, selectedPlanetId);
        if (sourcePlanet == null) return;
        
        float dx = currentPosition.x - sourcePlanet.position.x;
        float dy = currentPosition.y - sourcePlanet.position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) angle += 360;
        
        Player player = getPlayerForSelectedPlanet(state);
        int shipCount = player.ships.size();
        if (shipCount == 3) {
        	angle = angle + 30f; 
        }
        
        float sectorSize = 360f / shipCount;
        int selectedShipIndex = (int)((angle + sectorSize/2) % 360 / sectorSize);
        
        if (distance < EngineUtility.PLANET_RADIUS + radialSelectionThreshold) {
            selectedShipId = null;
            if (distance > EngineUtility.PLANET_RADIUS + radialHoverThreshold) {
            	hoveredShipId = selectedShipIndex;
            } else {
            	hoveredShipId = null;
            }
        } else {
            selectedShipId = selectedShipIndex;
            hoveredShipId = null;
        }
    }
	
	private void updateBuildingSelectionFromDrag(Vector3 currentPosition, GameState state) {
        Planet sourcePlanet = EngineUtility.getPlanet(state.planets, selectedPlanetId);
        if (sourcePlanet == null) return;
        
        float dx = currentPosition.x - sourcePlanet.position.x;
        float dy = currentPosition.y - sourcePlanet.position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) angle += 360;
        
        //Player player = getPlayerForSelectedPlanet(state);
        int buildingCount = 1;
        if (buildingCount == 3) {
        	angle = angle + 30f; 
        }
        
        float sectorSize = 360f / buildingCount;
        int selectedBuildingIndex = (int)((angle + sectorSize/2) % 360 / sectorSize);
        if (distance < EngineUtility.PLANET_RADIUS + radialSelectionThreshold) {
        	hoveredBuildingId = null;
            if (distance > EngineUtility.PLANET_RADIUS + radialHoverThreshold) {
            	hoveredBuildingId = selectedBuildingIndex;
            } else {
            	hoveredBuildingId = null;
            }
        } else {
        	if (hoveredBuildingId != null && hoveredBuildingId == 0) {
        		BuildTurretMessage order = new BuildTurretMessage();
    			order.gameUUID = state.gameUUID;
    			order.userUUID = API.authKey;
    			order.planetId = selectedPlanetId;
    			GameModel.outputMessageQueue.add(order);
        	}
            hoveredBuildingId = null;
            selectedPlanetId = null;
        }
    }
    
    private Player getPlayerForSelectedPlanet(GameState state) {
        if (selectedPlanetId == null) return null;
        Planet planet = EngineUtility.getPlanet(state.planets, selectedPlanetId);
        if (planet == null) return null;
        return EngineUtility.getPlayer(state.players, planet.ownerId);
    }
    
    private Vector3 getTouchWorldPosition(PerspectiveCamera camera) {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        float t = -ray.origin.z / ray.direction.z;
        return new Vector3(
            ray.origin.x + t * ray.direction.x,
            ray.origin.y + t * ray.direction.y,
            0f
        );
    }

    private boolean isTouchInsidePlanet(Vector3 worldClick, Planet planet) {
        return Vector3.dst(planet.position.x, planet.position.y, 0f, worldClick.x, worldClick.y, 0f) < EngineUtility.PLANET_RADIUS + HITBOX_INCREASE;
    }
    
    public Integer getSelectedPlanetId() {
        return selectedPlanetId;
    }
    
    public Integer getSelectedShipId() {
		return selectedShipId;
	}
    
    public Integer getHoveredShipId() {
		return hoveredShipId;
	}
    
    public Integer getHoveredBuildingId() {
		return hoveredBuildingId;
	}

    public Planet getHoveredPlanet(PerspectiveCamera camera, GameState state) {
        Vector3 touchWorld = getTouchWorldPosition(camera);
        for (Planet planet : state.planets) {
            if (Vector3.dst(touchWorld.x, touchWorld.y, 0, planet.position.x, planet.position.y, 0) < EngineUtility.PLANET_RADIUS + HITBOX_INCREASE) {
                return planet;
            }
        }
        return null;
    }

    public Vector3 getCurrentTouchWorldPosition(PerspectiveCamera camera) {
        return getTouchWorldPosition(camera);
    }
    
}