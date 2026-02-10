package de.instinct.eqfleet.game.frontend.input.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.net.message.types.GamePauseMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.input.InputHandler;
import de.instinct.eqfleet.game.frontend.input.model.GameInputModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;

public class GeneralInputHandler extends InputHandler {

	@Override
	public void handleInput(PerspectiveCamera camera, GameState state) {
        Rectangle timerBounds = GraphicsUtil.scaleFactorAdjusted(new Rectangle(320, 820, 80, 50));
        if (timerBounds.contains(InputUtil.getMousePosition()) && InputUtil.isClicked()) {
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
	
	private Planet getHoveredPlanet(PerspectiveCamera camera, GameState state) {
        for (Planet planet : state.planets) {
            if (isTouchInsidePlanet(GameInputModel.mouseWorldPos, planet)) {
                return planet;
            }
        }
        return null;
    }

    private boolean isTouchInsidePlanet(Vector3 worldClick, Planet planet) {
        return Vector3.dst(planet.position.x, planet.position.y, 0f, worldClick.x, worldClick.y, 0f) < EngineUtility.PLANET_RADIUS + GameInputModel.HITBOX_INCREASE;
    }

}
