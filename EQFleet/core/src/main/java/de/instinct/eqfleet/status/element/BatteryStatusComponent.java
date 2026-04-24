package de.instinct.eqfleet.status.element;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.status.StatusModel;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScale;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScaleLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class BatteryStatusComponent extends Component {
	
	private PlainRectangularLoadingBar batteryBar;
	private ColorScaleLoader colorScaleLoader;
	
	public BatteryStatusComponent() {
		super();
		colorScaleLoader = new ColorScaleLoader();
		batteryBar = new PlainRectangularLoadingBar();
		batteryBar.getBorder().setSize(1f);
		batteryBar.setMaxValue(1f);
		batteryBar.getDescriptorLabel().setColor(Color.WHITE);
		batteryBar.getDescriptorLabel().setType(FontType.TINY);
		batteryBar.getBackgroundShape().setColor(Color.BLACK);
	}

	@Override
	protected void updateComponent() {
		batteryBar.setBounds(getBounds());
		float batteryPercentage = StatusModel.batteryStatus.percentage() * 100f;
		boolean isCharging = StatusModel.batteryStatus.isCharging();
		String descriptor = StringUtils.format(batteryPercentage, 0) + (isCharging ? " \u26A1" : "");
		batteryBar.setCustomDescriptor(descriptor);
		batteryBar.getBorder().setColor(SkinManager.skinColor);
		batteryBar.setCurrentValue(StatusModel.batteryStatus.percentage());
		batteryBar.setColor(colorScaleLoader.load(ColorScale.GREEN_TO_RED, StatusModel.batteryStatus.percentage()));
	}

	@Override
	protected void renderComponent() {
		batteryBar.render();
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
		batteryBar.dispose();
	}

}
