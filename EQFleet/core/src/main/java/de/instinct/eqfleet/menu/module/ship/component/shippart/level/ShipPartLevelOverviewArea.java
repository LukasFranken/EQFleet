package de.instinct.eqfleet.menu.module.ship.component.shippart.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.module.ship.component.shippart.level.config.ShipPartLevelOverviewAreaConfig;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShipPartLevelOverviewArea extends Component {
	
	private ShipPartLevelOverviewAreaConfig config;
	
	private Label tagLabel;
	private Label minLabel;
	private Label maxLabel;
	private PlainRectangularLoadingBar partProgressBar;
	
	public ShipPartLevelOverviewArea(ShipPartLevelOverviewAreaConfig config) {
		super();
		this.config = config;
		
		tagLabel = new Label(config.getTag());
		tagLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		tagLabel.setType(FontType.SMALL);
		tagLabel.setColor(config.getPartColor());
		
		minLabel = new Label(config.getMinValueLabel());
		minLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		minLabel.setType(FontType.TINY);
		minLabel.setColor(config.getPartColor());
		maxLabel = new Label(config.getMaxValueLabel());
		maxLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		maxLabel.setType(FontType.TINY);
		maxLabel.setColor(config.getPartColor());
		
		partProgressBar = new PlainRectangularLoadingBar();
		partProgressBar.setBar(TextureManager.createTexture(config.getPartColor()));
		partProgressBar.setCurrentValue(50);
		partProgressBar.setMaxValue(100);
		Border barBorder = new Border();
		barBorder.setSize(1f);
		barBorder.setColor(config.getPartColor());
		partProgressBar.setBorder(barBorder);
		partProgressBar.setFixedHeight(5f);
		partProgressBar.setCustomDescriptor("");
	}
	
	@Override
	protected void updateComponent() {
		tagLabel.setFixedWidth(getBounds().width);
		tagLabel.setPosition(getBounds().x, getBounds().y + 10);
		minLabel.setFixedWidth(25f);
		minLabel.setPosition(getBounds().x, getBounds().y);
		maxLabel.setFixedWidth(25f);
		maxLabel.setPosition(getBounds().x + getBounds().width - 25f, getBounds().y);
		partProgressBar.setFixedWidth(getBounds().width - 60);
		partProgressBar.setPosition(getBounds().x + 30, getBounds().y);
	}
	
	@Override
	protected void renderComponent() {
		Color adjustedColor = new Color(config.getPartColor());
		adjustedColor.a = 0.5f;
		Shapes.draw(EQRectangle.builder()
				.bounds(new Rectangle(getBounds().x + 5f, getBounds().y + 25f, getBounds().width - 10, 1f))
				.color(adjustedColor)
				.build());
		tagLabel.render();
		minLabel.render();
		maxLabel.render();
		partProgressBar.render();
	}

	@Override
	public float calculateHeight() {
		return 200f;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		
	}

}
