package de.instinct.eqfleet.mining.frontend.hud.element;

import com.badlogic.gdx.graphics.Color;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.eqfleet.holo.HoloPanel;
import de.instinct.eqfleet.holo.HoloRenderer;
import de.instinct.eqfleet.holo.style.HoloPanelGlowConfiguration;
import de.instinct.eqfleet.holo.style.HoloPanelStyle;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class EnergyHudElement extends Component {
	
	private Image energyIcon;
	private HoloPanel iconHoloPanel;
	private HoloPanel barHoloPanel;
	private BoxedRectangularLoadingBar energyBar;
	
	public EnergyHudElement() {
		super();
		energyIcon = new Image(TextureManager.getTexture("ui/image", "energy"));
		energyBar = new BoxedRectangularLoadingBar();
		energyBar.getBorder().setSize(0f);
		energyBar.getBackgroundShape().setColor(new Color(0f, 0f, 0f, 1f));
		energyBar.setSegments(10);
		energyBar.setPartialSegments(true);
		energyBar.setDirection(Direction.WEST);
		energyBar.getBackgroundShape().setColor(new Color(0f, 0f, 0f, 1f));
		
		iconHoloPanel = HoloPanel.builder()
				.color(new Color(0f, 0.6f, 0.18f, 1f))
				.style(HoloPanelStyle.builder()
						.fillAlpha(0.05f)
						.reflectionStrength(0f)
						.borderSize(1f)
						.glowConfiguration(HoloPanelGlowConfiguration.builder()
								.glowSize(15f)
								.glowAnimationSpeed(2f)
								.glowAnimationStrength(0f)
								.build())
						.build())
				.build();
		
		barHoloPanel = HoloPanel.builder()
				.color(new Color(0f, 0.6f, 0.18f, 1f))
				.style(HoloPanelStyle.builder()
						.fillAlpha(0.05f)
						.reflectionStrength(0f)
						.borderSize(1f)
						.glowConfiguration(HoloPanelGlowConfiguration.builder()
								.glowSize(15f)
								.glowAnimationSpeed(2f)
								.glowAnimationStrength(0f)
								.build())
						.build())
				.build();
	}
	
	@Override
	protected void updateComponent() {
		MiningPlayerShip ship = MiningEngineAPI.getShip(MiningModel.playerId);
		energyBar.setMaxValue(ship.core.maxCharge);
		energyBar.setCurrentValue(ship.core.currentCharge);
		energyBar.setBounds(41, GraphicsUtil.screenBounds().height - 90, 80, 6);
		barHoloPanel.setBounds(41, GraphicsUtil.screenBounds().height - 90, 80, 6);
		energyIcon.setBounds(122, GraphicsUtil.screenBounds().height - 90, 16, 16);
		iconHoloPanel.setBounds(122, GraphicsUtil.screenBounds().height - 90, 16, 16);
	}

	@Override
	protected void renderComponent() {
		energyBar.render();
		energyIcon.render();
		HoloRenderer.drawPanel(iconHoloPanel);
		HoloRenderer.drawPanel(barHoloPanel);
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
		energyIcon.dispose();
		energyBar.dispose();
	}
	
}
