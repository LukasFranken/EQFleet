package de.instinct.eqfleet.mining.frontend.hud.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.frontend.hud.element.model.CustomJoystick;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;

public class MobileInputHud extends Component {
	
	private CustomJoystick joystick;
    private ColorButton shootButton;
	
	public MobileInputHud() {
		super();
		joystick = new CustomJoystick(80, 150, 80);
    	shootButton = new ColorButton("FIRE");
    	shootButton.setColor(new Color(Color.GRAY));
    	shootButton.getColor().a = 0.5f;
    	shootButton.getLabel().setColor(Color.RED);
    	shootButton.setBounds(320, 120, 60, 40);
    	shootButton.setDownAction(new Action() {
			
			@Override
			public void execute() {
				MiningModel.mobileInput.shoot = true;
			}
			
		});
    	shootButton.setUpAction(new Action() {
			
			@Override
			public void execute() {
				MiningModel.mobileInput.shoot = false;
			}
			
		});
	}
	
	@Override
	protected void updateComponent() {
		joystick.update();
    	Vector2 direction = joystick.getDirection();
    	MiningModel.mobileInput.up = direction.y > 0.3f;
        MiningModel.mobileInput.down = direction.y < -0.3f;
        MiningModel.mobileInput.left = direction.x < -0.3f;
        MiningModel.mobileInput.right = direction.x > 0.3f;
	}

	@Override
	protected void renderComponent() {
		joystick.render();
		shootButton.render();
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		joystick.dispose();
		shootButton.dispose();
	}
	
}
