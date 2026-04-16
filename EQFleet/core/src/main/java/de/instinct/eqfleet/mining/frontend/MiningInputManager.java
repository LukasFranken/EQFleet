
package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.mining.net.message.InputChangedOrder;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.input.MiningInput;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.PlatformUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;

public class MiningInputManager {

    private MiningInput lastFrameInput;
    private MiningInput input;
    private CustomJoystick joystick;
    private ColorButton shootButton;

    public MiningInputManager() {
        lastFrameInput = new MiningInput();
        input = new MiningInput();
        if (PlatformUtil.isMobile()) {
        	joystick = new CustomJoystick(250, 250, 250);
        	shootButton = new ColorButton("FIRE");
        	shootButton.setColor(new Color(Color.GRAY));
        	shootButton.getColor().a = 0.5f;
        	shootButton.getLabel().setColor(Color.RED);
        	shootButton.setBounds(320, 50, 60, 40);
        	shootButton.setDownAction(new Action() {
    			
    			@Override
    			public void execute() {
    				input.shoot = true;
    			}
    			
    		});
        	shootButton.setUpAction(new Action() {
    			
    			@Override
    			public void execute() {
    				input.shoot = false;
    			}
    			
    		});
        }
    }

    public void update() {
        lastFrameInput.up = input.up;
        lastFrameInput.down = input.down;
        lastFrameInput.left = input.left;
        lastFrameInput.right = input.right;
        lastFrameInput.shoot = input.shoot;

        // Handle WASD input
        input.up = InputUtil.isDown(Keys.W);
        input.down = InputUtil.isDown(Keys.S);
        input.left = InputUtil.isDown(Keys.A);
        input.right = InputUtil.isDown(Keys.D);
        input.shoot = InputUtil.isDown(Keys.SPACE);

        // Handle joystick input
        if (joystick != null) {
        	joystick.update();
        	joystick.render();
        	shootButton.render();
        	Vector2 direction = joystick.getDirection();
            if (direction.len() > 0) {
                input.up = direction.y > 0.3f;
                input.down = direction.y < -0.3f;
                input.left = direction.x < -0.3f;
                input.right = direction.x > 0.3f;
            }
        }

        // Send input changes if necessary
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

        // Handle escape key for menu
        if (InputUtil.isDown(Keys.ESCAPE)) {
            SceneManager.changeTo(SceneType.MENU);
        }
    }

    public void dispose() {
        joystick.dispose();
        shootButton.dispose();
    }
}
