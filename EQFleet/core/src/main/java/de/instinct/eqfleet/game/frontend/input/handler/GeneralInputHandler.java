package de.instinct.eqfleet.game.frontend.input.handler;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.net.message.types.GamePauseMessage;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.input.InputHandler;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;

public class GeneralInputHandler extends InputHandler {

	@Override
	public void handleInput(PerspectiveCamera camera, GameState state) {
        Rectangle timerBounds = GraphicsUtil.scaleFactorAdjusted(new Rectangle(320, 820, 80, 50));
        if (timerBounds.contains(InputUtil.getMousePosition())) {
        	GamePauseMessage order = new GamePauseMessage();
        	order.gameUUID = state.gameUUID;
        	order.userUUID = API.authKey;
        	order.reason = "Manual Pause";
        	order.pause = true;
        	GameModel.outputMessageQueue.add(order);
        }
	}

}
