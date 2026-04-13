package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.Input.Keys;

import de.instinct.engine.mining.net.message.InputChangedOrder;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.input.MiningInput;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.InputUtil;

public class MiningInputManager {
	
	private MiningInput lastFrameInput;
	private MiningInput input;
	
	public MiningInputManager() {
		lastFrameInput = new MiningInput();
		input = new MiningInput();
	}

	public void update() {
		lastFrameInput.up = input.up;
		lastFrameInput.down = input.down;
		lastFrameInput.left = input.left;
		lastFrameInput.right = input.right;
		lastFrameInput.shoot = input.shoot;
		
		if (InputUtil.isDown(Keys.W)) {
			input.up = true;
		} else {
			input.up = false;
		}
		
		if (InputUtil.isDown(Keys.S)) {
			input.down = true;
		} else {
			input.down = false;
		}
		
		if (InputUtil.isDown(Keys.A)) {
			input.left = true;
		} else {
			input.left = false;
		}
		
		if (InputUtil.isDown(Keys.D)) {
			input.right = true;
		} else {
			input.right = false;
		}
		
		if (InputUtil.isPressed()) {
			input.shoot = true;
		} else {
			input.shoot = false;
		}
		
		if (!input.isIdenticalTo(lastFrameInput)) {
			InputChangedOrder inputChangedOrder = new InputChangedOrder();
			inputChangedOrder.playerId = 1;
			inputChangedOrder.forward = input.up;
			inputChangedOrder.backward = input.down;
			inputChangedOrder.left = input.left;
			inputChangedOrder.right = input.right;
			inputChangedOrder.shoot = input.shoot;
			MiningEngineAPI.addOrder(MiningModel.state, inputChangedOrder);
		}
		
		if (InputUtil.isDown(Keys.ESCAPE)) {
			SceneManager.changeTo(SceneType.MENU);
		}
	}

}
