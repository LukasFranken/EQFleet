
package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.Input.Keys;

import de.instinct.engine.mining.net.message.InputChangedOrder;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.input.MiningInput;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.PlatformUtil;

public class MiningInputManager {

    private MiningInput lastFrameInput;

    public MiningInputManager() {
        lastFrameInput = new MiningInput();
        MiningModel.input = new MiningInput();
        MiningModel.mobileInput = new MiningInput();
    }

    public void update() {
        lastFrameInput.up = MiningModel.input.up;
        lastFrameInput.down = MiningModel.input.down;
        lastFrameInput.left = MiningModel.input.left;
        lastFrameInput.right = MiningModel.input.right;
        lastFrameInput.shoot = MiningModel.input.shoot;

        if (!PlatformUtil.isMobile()) {
        	MiningModel.input.up = MiningModel.mobileInput.up;
			MiningModel.input.down = MiningModel.mobileInput.down;
			MiningModel.input.left = MiningModel.mobileInput.left;
			MiningModel.input.right = MiningModel.mobileInput.right;
			MiningModel.input.shoot = MiningModel.mobileInput.shoot;
        } else {
        	MiningModel.input.up = InputUtil.isDown(Keys.W);
            MiningModel.input.down = InputUtil.isDown(Keys.S);
            MiningModel.input.left = InputUtil.isDown(Keys.A);
            MiningModel.input.right = InputUtil.isDown(Keys.D);
            MiningModel.input.shoot = InputUtil.isDown(Keys.SPACE);
        }
        
        if (!MiningModel.input.isIdenticalTo(lastFrameInput)) {
            InputChangedOrder inputChangedOrder = new InputChangedOrder();
            inputChangedOrder.playerId = MiningModel.playerId;
            inputChangedOrder.forward = MiningModel.input.up;
            inputChangedOrder.backward = MiningModel.input.down;
            inputChangedOrder.left = MiningModel.input.left;
            inputChangedOrder.right = MiningModel.input.right;
            inputChangedOrder.shoot = MiningModel.input.shoot;
            MiningEngineAPI.addOrder(MiningModel.state, inputChangedOrder);
        }

        if (InputUtil.isDown(Keys.ESCAPE)) {
            SceneManager.changeTo(SceneType.MENU);
        }
    }
    
}
