package de.instinct.eqfleet.mining.frontend.hud.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class EngineHudElement extends Component {
	
	private PlainRectangularLoadingBar positiveSpeedBar;
	private PlainRectangularLoadingBar negativeSpeedBar;
	private EQRectangle speedBarBorder;
	private Rectangle speedBarBounds;
	
	private PlainRectangularLoadingBar rightTurnBar;
	private PlainRectangularLoadingBar leftTurnBar;
	private EQRectangle turnBarBorder;
	private Rectangle turnBarBounds;
	
	public EngineHudElement() {
		super();
		Border barBorder = new Border();
		barBorder.setColor(new Color(GameConfig.teammate1Color));
		barBorder.setSize(1f);
		
		speedBarBounds = new Rectangle();
		speedBarBorder = EQRectangle.builder()
				.color(new Color(GameConfig.teammate1Color))
				.thickness(2f)
				.build();
		
		positiveSpeedBar = new PlainRectangularLoadingBar();
		positiveSpeedBar.setColor(new Color(1f, 1f, 1f, 1f));
		positiveSpeedBar.getBorder().setSize(0f);
		positiveSpeedBar.setCustomDescriptor("");
		
		negativeSpeedBar = new PlainRectangularLoadingBar();
		negativeSpeedBar.setColor(new Color(1f, 1f, 1f, 1f));
		negativeSpeedBar.setDirection(Direction.WEST);
		negativeSpeedBar.getBorder().setSize(0f);
		negativeSpeedBar.setCustomDescriptor("");
		
		turnBarBounds = new Rectangle();
		turnBarBorder = EQRectangle.builder()
				.color(new Color(GameConfig.teammate1Color))
				.thickness(2f)
				.build();
		
		rightTurnBar = new PlainRectangularLoadingBar();
		rightTurnBar.setColor(new Color(1f, 1f, 1f, 1f));
		rightTurnBar.getBorder().setSize(0f);
		rightTurnBar.setCustomDescriptor("");
		
		leftTurnBar = new PlainRectangularLoadingBar();
		leftTurnBar.setColor(new Color(1f, 1f, 1f, 1f));
		leftTurnBar.setDirection(Direction.WEST);
		leftTurnBar.getBorder().setSize(0f);
		leftTurnBar.setCustomDescriptor("");
	}
	
	@Override
	protected void updateComponent() {
		MiningPlayerShip ship = MiningEngineAPI.getShip(MiningModel.playerId);
		
		speedBarBounds.set(20, 20, GraphicsUtil.screenBounds().width - 40, 10);
		speedBarBorder.setBounds(speedBarBounds);
		positiveSpeedBar.setMaxValue(ship.thruster.maxSpeed);
		positiveSpeedBar.setCurrentValue(Math.max(0, ship.speed));
		positiveSpeedBar.setBounds(speedBarBounds.x + (speedBarBounds.width / 2f), speedBarBounds.y + speedBarBorder.getThickness(), (speedBarBounds.width / 2f) - speedBarBorder.getThickness(), speedBarBounds.height - (speedBarBorder.getThickness() * 2));
		positiveSpeedBar.getBarColor().r = 1 - (positiveSpeedBar.getCurrentValue() / positiveSpeedBar.getMaxValue());
		positiveSpeedBar.getBarColor().g = 1 - (positiveSpeedBar.getCurrentValue() / positiveSpeedBar.getMaxValue());
		negativeSpeedBar.setMaxValue(Math.abs(ship.thruster.maxReverseSpeed));
		if (ship.speed < 0) {
			negativeSpeedBar.setCurrentValue(Math.max(0, -ship.speed));
			negativeSpeedBar.getBarColor().g = 1 - (negativeSpeedBar.getCurrentValue() / negativeSpeedBar.getMaxValue());
			negativeSpeedBar.getBarColor().b = 1 - (negativeSpeedBar.getCurrentValue() / negativeSpeedBar.getMaxValue());
		} else {
			negativeSpeedBar.setCurrentValue(0);
		}
		negativeSpeedBar.setBounds(speedBarBounds.x, speedBarBounds.y + speedBarBorder.getThickness(), (speedBarBounds.width / 2f) - speedBarBorder.getThickness(), speedBarBounds.height - (speedBarBorder.getThickness() * 2));
		
		turnBarBounds.set(speedBarBounds.x, speedBarBounds.y + speedBarBorder.getThickness() - 6, speedBarBounds.width, 6);
		turnBarBorder.setBounds(turnBarBounds);
		leftTurnBar.setMaxValue(ship.thruster.maxRotationSpeed);
		leftTurnBar.setCurrentValue(Math.max(0, ship.thruster.rotationSpeed));
		leftTurnBar.setBounds(turnBarBounds.x, turnBarBounds.y + turnBarBorder.getThickness(), (turnBarBounds.width / 2f) - turnBarBorder.getThickness(), turnBarBounds.height - (turnBarBorder.getThickness() * 2));
		leftTurnBar.getBarColor().r = 1 - (leftTurnBar.getCurrentValue() / leftTurnBar.getMaxValue());
		leftTurnBar.getBarColor().g = 1 - (leftTurnBar.getCurrentValue() / leftTurnBar.getMaxValue());
		rightTurnBar.setMaxValue(Math.abs(ship.thruster.maxRotationSpeed));
		if (ship.thruster.rotationSpeed < 0) {
			rightTurnBar.setCurrentValue(Math.max(0, -ship.thruster.rotationSpeed));
			rightTurnBar.getBarColor().r = 1 - (rightTurnBar.getCurrentValue() / rightTurnBar.getMaxValue());
			rightTurnBar.getBarColor().g = 1 - (rightTurnBar.getCurrentValue() / leftTurnBar.getMaxValue());
		} else {
			rightTurnBar.setCurrentValue(0);
		}
		rightTurnBar.setBounds(turnBarBounds.x + (turnBarBounds.width / 2f), turnBarBounds.y + turnBarBorder.getThickness(), (turnBarBounds.width / 2f) - turnBarBorder.getThickness(), turnBarBounds.height - (turnBarBorder.getThickness() * 2));
	}

	@Override
	protected void renderComponent() {
		positiveSpeedBar.render();
		negativeSpeedBar.render();
		Shapes.draw(speedBarBorder);
		rightTurnBar.render();
		leftTurnBar.render();
		Shapes.draw(turnBarBorder);
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
		positiveSpeedBar.dispose();
		negativeSpeedBar.dispose();
		rightTurnBar.dispose();
		leftTurnBar.dispose();
	}

}
