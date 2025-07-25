package de.instinct.eqfleet.game.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqlibgdxutils.InputUtil;

public class GameInputManager {
	
	private float HITBOX_INCREASE = 50f;
	
	private Integer selectedPlanetId = null;
	private Integer hoveredShipId = null;
    private Integer selectedShipId = null;
    
    private Vector3 dragStartPosition = new Vector3();
    private boolean isDragging = false;
    private float shipSelectionThreshold = 40f;
    
    public void handleInput(PerspectiveCamera camera, GameState state) {
        if (!GameModel.inputEnabled) {
            return;
        }
        Vector3 worldTouch = getTouchWorldPosition(camera);

        if (InputUtil.isClicked()) {
            for (Planet planet : state.planets) {
                if (planet.ownerId == GameModel.playerId && isClickInsidePlanet(worldTouch, planet)) {
                    selectedPlanetId = planet.id;
                    dragStartPosition.set(worldTouch);
                    isDragging = true;
                    selectedShipId = null; // Reset ship selection
                    break;
                }
            }
        }
        
        if (Gdx.input.isTouched() && selectedPlanetId != null && isDragging) {
            Player player = getPlayerForSelectedPlanet(state);
            if (player != null && player.ships.size() > 1 && selectedShipId == null) {
                updateShipSelectionFromDrag(worldTouch, state);
            }
        }

        if (!Gdx.input.isTouched() && selectedPlanetId != null) {
            isDragging = false;
            for (Planet planet : state.planets) {
                if (planet.id != selectedPlanetId && isClickInsidePlanet(worldTouch, planet)) {
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
            selectedPlanetId = null;
            selectedShipId = null;
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
        
        if (distance < EngineUtility.PLANET_RADIUS + shipSelectionThreshold) {
            selectedShipId = null;
            if (distance > EngineUtility.PLANET_RADIUS) {
            	hoveredShipId = selectedShipIndex;
            } else {
            	hoveredShipId = null;
            }
        } else {
            selectedShipId = selectedShipIndex;
            hoveredShipId = null;
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

    private boolean isClickInsidePlanet(Vector3 worldClick, Planet planet) {
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