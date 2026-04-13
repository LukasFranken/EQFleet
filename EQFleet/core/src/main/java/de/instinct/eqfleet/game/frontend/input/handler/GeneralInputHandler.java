package de.instinct.eqfleet.game.frontend.input.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.instinct.api.core.API;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.net.messages.GamePauseMessage;
import de.instinct.engine_api.core.service.EngineDataInterface;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.input.InputHandler;
import de.instinct.eqfleet.game.frontend.input.model.GameInputModel;
import de.instinct.eqlibgdxutils.InputUtil;

public class GeneralInputHandler extends InputHandler {
	
	private Rectangle timerBounds;
	
	public GeneralInputHandler() {
		timerBounds = new Rectangle(320, 820, 80, 50);
	}

	@Override
	public void handleInput(PerspectiveCamera camera, FleetGameState state) {
        if (timerBounds.contains(InputUtil.getVirtualMousePosition()) && InputUtil.isClicked()) {
        	GamePauseMessage order = new GamePauseMessage();
        	order.gameUUID = state.gameUUID;
        	order.userUUID = API.authKey;
        	order.reason = "Manual Pause";
        	order.pause = true;
        	GameModel.outputMessageQueue.add(order);
        }
        
        GameInputModel.mouseWorldPos = getTouchWorldPosition(camera);
        GameInputModel.targetedPlanet = getHoveredPlanet(camera, state);
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
	
	private Planet getHoveredPlanet(PerspectiveCamera camera, FleetGameState state) {
        for (Planet planet : state.entityData.planets) {
            if (isTouchInsidePlanet(GameInputModel.mouseWorldPos, planet)) {
                return planet;
            }
        }
        return null;
    }

    private boolean isTouchInsidePlanet(Vector3 worldClick, Planet planet) {
        return Vector3.dst(planet.position.x, planet.position.y, 0f, worldClick.x, worldClick.y, 0f) < EngineDataInterface.PLANET_RADIUS + GameInputModel.HITBOX_INCREASE;
    }

}
