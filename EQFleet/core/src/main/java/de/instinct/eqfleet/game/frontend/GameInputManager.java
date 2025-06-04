package de.instinct.eqfleet.game.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;

public class GameInputManager {
	
	private float HITBOX_INCREASE = 50f;
	
	private Integer selectedPlanetId = null;

	public void handleInput(PerspectiveCamera camera, GameState state) {
		if (!GameModel.inputEnabled) {
			return;
		}
	    Vector3 worldTouch = getTouchWorldPosition(camera);

	    if (Gdx.input.justTouched()) {
	        for (Planet planet : state.planets) {
	            if (planet.ownerId == GameModel.playerId && isClickInsidePlanet(worldTouch, planet)) {
	                selectedPlanetId = planet.id;
	                break;
	            }
	        }
	    }

	    if (!Gdx.input.isTouched() && selectedPlanetId != null) {
	        for (Planet planet : state.planets) {
	            if (planet.id != selectedPlanetId && isClickInsidePlanet(worldTouch, planet)) {
	                FleetMovementMessage order = new FleetMovementMessage();
	                order.gameUUID = state.gameUUID;
	                order.userUUID = API.authKey;
	                order.fromPlanetId = selectedPlanetId;
	                order.toPlanetId = planet.id;
	                GameModel.outputMessageQueue.add(order);
	                break;
	            }
	        }
	        selectedPlanetId = null;
	    }
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