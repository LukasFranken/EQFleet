package de.instinct.eqfleet.mining.frontend;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.entity.ship.cargo.CargoItem;
import de.instinct.engine.mining.net.message.RecallOrder;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.PlatformUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.BarFragment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.FragmentRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class MiningHudRenderer {
	
	private CustomJoystick joystick;
    private ColorButton shootButton;
    private ColorButton recallButton;
    
	private BoxedRectangularLoadingBar chargeBar;
	private FragmentRectangularLoadingBar cargoBar;
	
	private List<BarFragment> cargoFragments;
	private Label workingLabel;
	
	public void init() {
		Border barBorder = new Border();
		barBorder.setColor(GameConfig.teammate1Color);
		barBorder.setSize(1f);
		
		chargeBar = new BoxedRectangularLoadingBar();
		chargeBar.setBorder(barBorder);
		
		cargoBar = new FragmentRectangularLoadingBar();
		cargoBar.setBorder(barBorder);
		
		cargoFragments = new ArrayList<>();
		
		workingLabel = new Label("");
		workingLabel.setType(FontType.TINY);
		workingLabel.setColor(Color.GRAY);
		workingLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		recallButton = new ColorButton("RECALL");
		recallButton.setColor(new Color(0.5f, 0f, 0f, 0.5f));
		recallButton.setDownColor(new Color());
		recallButton.setHoverColor(new Color());
		recallButton.getLabel().setColor(Color.GRAY);
		recallButton.getLabel().setType(FontType.BOLD);
		recallButton.setGlowAnimation(true);
		recallButton.getBorder().setColor(Color.GRAY);
		recallButton.setAction(new Action() {
			
			@Override
			public void execute() {
				RecallOrder recallOrder = new RecallOrder();
				recallOrder.playerId = MiningModel.playerId;
				MiningEngineAPI.addOrder(MiningModel.state, recallOrder);
			}
			
		});
		
		if (PlatformUtil.isMobile()) {
        	joystick = new CustomJoystick(80, 80, 80);
        	shootButton = new ColorButton("FIRE");
        	shootButton.setColor(new Color(Color.GRAY));
        	shootButton.getColor().a = 0.5f;
        	shootButton.getLabel().setColor(Color.RED);
        	shootButton.setBounds(320, 50, 60, 40);
        	shootButton.setDownAction(new Action() {
    			
    			@Override
    			public void execute() {
    				MiningModel.input.shoot = true;
    			}
    			
    		});
        	shootButton.setUpAction(new Action() {
    			
    			@Override
    			public void execute() {
    				MiningModel.input.shoot = false;
    			}
    			
    		});
        }
	}
	
	public void update() {
		MiningPlayerShip ship = MiningModel.state.entityData.playerShips.get(0);
		chargeBar.setSegments(10);
		chargeBar.setMaxValue(ship.core.maxCharge);
		chargeBar.setCurrentValue(ship.core.currentCharge);
		chargeBar.setPartialSegments(true);
		chargeBar.setBounds(20, GraphicsUtil.screenBounds().height - 80, 160, 6);
		
		updateFragmentsForCargo(ship);
		cargoBar.setMaxValue(ship.cargo.capacity);
		cargoBar.setFragments(cargoFragments);
		cargoBar.setBounds(20, GraphicsUtil.screenBounds().height - 86, 160, 6);
		
		if (joystick != null) {
        	joystick.update();
        	Vector2 direction = joystick.getDirection();
            if (direction.len() > 0) {
                MiningModel.input.up = direction.y > 0.3f;
                MiningModel.input.down = direction.y < -0.3f;
                MiningModel.input.left = direction.x < -0.3f;
                MiningModel.input.right = direction.x > 0.3f;
            }
        }
		
		recallButton.setBounds(160, 20, 80, 40);
		recallButton.setGlowAnimation(!ship.recalled);
		if (ship.recalled) {
			recallButton.getHoverColor().set(0.3f, 0f, 0f, 0.5f);
			recallButton.getDownColor().set(0.5f, 0f, 0f, 0.7f);
			recallButton.getLabel().setText("CANCEL");
		} else {
			recallButton.getHoverColor().set(0f, 0f, 0.3f, 0.5f);
			recallButton.getDownColor().set(0f, 0f, 0.7f, 0.7f);
			recallButton.getLabel().setText("RECALL");
		}
	}
	
	private void updateFragmentsForCargo(MiningPlayerShip ship) {
		cargoFragments.clear();
		for (CargoItem item : ship.cargo.items) {
			cargoFragments.add(BarFragment.builder()
					.value(item.amount)
					.color(OreManager.getColorForResourceType(item.resourceType))
					.build());
		}
	}

	public void render() {
		chargeBar.render();
		cargoBar.render();
		
		MiningPlayerShip ship = MiningModel.state.entityData.playerShips.get(0);
		workingLabel.setBounds(20, GraphicsUtil.screenBounds().height - 96, 160, 10);
		workingLabel.setText("SPEED: " + StringUtils.format(ship.speed, 1));
		workingLabel.render();
		workingLabel.setBounds(20, GraphicsUtil.screenBounds().height - 106, 160, 10);
		workingLabel.setText("TURN: " + StringUtils.format(ship.thruster.rotationSpeed, 1));
		workingLabel.render();
		
		if (joystick != null) {
			joystick.render();
			shootButton.render();
		}
		if (MiningEngineAPI.shipIsRecallable(MiningModel.playerId)) recallButton.render();
	}
	
	public void dispose() {
		chargeBar.dispose();
		cargoBar.dispose();
		recallButton.dispose();
		if (joystick != null) {
			joystick.dispose();
			shootButton.dispose();
		}
	}

}
