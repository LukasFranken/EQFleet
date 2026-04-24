package de.instinct.eqfleet.status.element;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.holo.HoloPanel;
import de.instinct.eqfleet.holo.HoloRenderer;
import de.instinct.eqfleet.holo.style.HoloPanelGlowConfiguration;
import de.instinct.eqfleet.holo.style.HoloPanelStyle;
import de.instinct.eqfleet.status.StatusModel;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScale;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScaleLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class BatteryStatusComponent extends Component {
	
	private HoloPanel batteryBackgroundHoloPanel;
	private PlainRectangularLoadingBar batteryBar;
	private ColorScaleLoader colorScaleLoader;
	
	public BatteryStatusComponent() {
		super();
		batteryBackgroundHoloPanel = HoloPanel.builder()
				.color(new Color(0f, 0.5f, 0f, 1f))
				.style(HoloPanelStyle.builder()
						.reflectionStrength(0f)
						.borderSize(1f)
						.glowConfiguration(HoloPanelGlowConfiguration.builder()
								.glowAnimationSpeed(2f)
								.glowAnimationStrength(1f)
								.build())
						.build())
				.build();
		colorScaleLoader = new ColorScaleLoader();
		batteryBar = new PlainRectangularLoadingBar();
		batteryBar.setMaxValue(1f);
		batteryBar.getBorder().setColor(new Color());
		batteryBar.getBorder().setSize(1f);
		batteryBar.getDescriptorLabel().setColor(Color.WHITE);
		batteryBar.getDescriptorLabel().setType(FontType.TINY);
		batteryBar.getBackgroundShape().setColor(Color.BLACK);
	}

	@Override
	protected void updateComponent() {
		batteryBar.setBounds(getBounds());
		float batteryPercentage = StatusModel.batteryStatus.percentage() * 100f;
		batteryBackgroundHoloPanel.setBounds(getBounds());
		if (StatusModel.batteryStatus.isCharging()) {
			batteryBackgroundHoloPanel.getStyle().getGlowConfiguration().setGlowSize(15f);
		} else {
			batteryBackgroundHoloPanel.getStyle().getGlowConfiguration().setGlowSize(1f);
		}
		batteryBar.setCustomDescriptor(StringUtils.format(batteryPercentage, 0));
		batteryBar.setCurrentValue(StatusModel.batteryStatus.percentage());
		batteryBar.setColor(colorScaleLoader.load(ColorScale.GREEN_TO_RED, StatusModel.batteryStatus.percentage()));
	}

	@Override
	protected void renderComponent() {
		HoloRenderer.drawPanel(batteryBackgroundHoloPanel);
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
