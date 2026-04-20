
package de.instinct.eqfleet.mining.input;

import com.badlogic.gdx.Input.Keys;

import de.instinct.engine.mining.order.InputChangedOrder;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.PlatformUtil;

public class MiningInputManager {

    private MiningInput lastFrameInput;

    public MiningInputManager() {
        lastFrameInput = new MiningInput();
        MiningInputModel.input = new MiningInput();
        MiningInputModel.mobileInput = new MiningInput();
    }

    public void update() {
        lastFrameInput.up = MiningInputModel.input.up;
        lastFrameInput.down = MiningInputModel.input.down;
        lastFrameInput.left = MiningInputModel.input.left;
        lastFrameInput.right = MiningInputModel.input.right;
        lastFrameInput.shoot = MiningInputModel.input.shoot;

        if (PlatformUtil.isMobile()) {
        	MiningInputModel.input.up = MiningInputModel.mobileInput.up;
        	MiningInputModel.input.down = MiningInputModel.mobileInput.down;
        	MiningInputModel.input.left = MiningInputModel.mobileInput.left;
        	MiningInputModel.input.right = MiningInputModel.mobileInput.right;
        	MiningInputModel.input.shoot = MiningInputModel.mobileInput.shoot;
        } else {
        	MiningInputModel.input.up = InputUtil.isDown(Keys.W);
        	MiningInputModel.input.down = InputUtil.isDown(Keys.S);
        	MiningInputModel.input.left = InputUtil.isDown(Keys.A);
            MiningInputModel.input.right = InputUtil.isDown(Keys.D);
            MiningInputModel.input.shoot = InputUtil.isDown(Keys.SPACE);
        }
        
        if (!MiningInputModel.input.isIdenticalTo(lastFrameInput)) {
            InputChangedOrder inputChangedOrder = new InputChangedOrder();
            inputChangedOrder.playerId = MiningModel.playerId;
            inputChangedOrder.forward = MiningInputModel.input.up;
            inputChangedOrder.backward = MiningInputModel.input.down;
            inputChangedOrder.left = MiningInputModel.input.left;
            inputChangedOrder.right = MiningInputModel.input.right;
            inputChangedOrder.shoot = MiningInputModel.input.shoot;
            MiningModel.inputOrders.add(inputChangedOrder);
        }

        if (InputUtil.isDown(Keys.ESCAPE)) {
            SceneManager.changeTo(SceneType.MENU);
        }
    }
    
}
