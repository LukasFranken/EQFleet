package de.instinct.eqfleet.menu.module.ship.component.shippart.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.module.ship.component.shippart.level.config.ShipPartLevelOverviewAreaConfig;
import de.instinct.eqfleet.menu.module.ship.component.shippart.level.levelupinfo.LevelUpInfoSection;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
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
	
	private Color adjustedColor;
	private Label componentLabel;
	private Label componentDescriptionLabel;
	private LevelUpInfoSection levelUpInfoSection;
	private Label tagLabel;
	private Label minLabel;
	private Label maxLabel;
	private PlainRectangularLoadingBar partProgressBar;
	private ColorButton levelUpButton;
	
	public ShipPartLevelOverviewArea(ShipPartLevelOverviewAreaConfig config) {
		super();
		this.config = config;
		
		adjustedColor = new Color(config.getPartColor());
		adjustedColor.a = 0.5f;
		
		Border border = new Border();
		border.setSize(1f);
		border.setColor(adjustedColor);
		
		componentLabel = new Label(config.getComponentType());
		componentLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		componentLabel.setType(FontType.NORMAL);
		componentLabel.setColor(config.getPartColor());
		
		componentDescriptionLabel = new Label(config.getComponentDescription());
		componentDescriptionLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		componentDescriptionLabel.setType(FontType.SMALL);
		componentDescriptionLabel.setColor(config.getPartColor());
		componentDescriptionLabel.setBorder(border);
		
		levelUpInfoSection = new LevelUpInfoSection(config.getInfoSectionConfig());
		
		tagLabel = new Label(config.getTag());
		tagLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		tagLabel.setType(FontType.SMALL);
		tagLabel.setColor(config.getPartColor());
		
		minLabel = new Label(StringUtils.formatBigNumber((long)config.getCurrentValue(), 0));
		minLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		minLabel.setType(FontType.TINY);
		minLabel.setColor(config.getPartColor());
		
		maxLabel = new Label(StringUtils.formatBigNumber((long)config.getMaxValue() == -1 ? (long)config.getCurrentValue() : (long)config.getMaxValue(), 0));
		maxLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		maxLabel.setType(FontType.TINY);
		maxLabel.setColor(config.getPartColor());
		
		partProgressBar = new PlainRectangularLoadingBar();
		partProgressBar.setBar(TextureManager.createTexture(config.getPartColor()));
		partProgressBar.setCurrentValue(config.getCurrentValue());
		partProgressBar.setMaxValue(config.getMaxValue() == -1 ? config.getCurrentValue() : config.getMaxValue());
		partProgressBar.setBorder(border);
		partProgressBar.setFixedHeight(5f);
		partProgressBar.setCustomDescriptor("");
		
		if (config.getLevelUpAction() != null) {
			levelUpButton = DefaultButtonFactory.colorButton("Upgrade", config.getLevelUpAction());
			levelUpButton.getBorder().setColor(config.getPartColor());
			levelUpButton.setAction(config.getLevelUpAction());
			levelUpButton.setLayer(2);
			levelUpButton.getLabel().setColor(config.getPartColor());
			levelUpButton.setHoverColor(new Color(adjustedColor.r, adjustedColor.g, adjustedColor.b, 0.2f));
			levelUpButton.setDownColor(adjustedColor);
		}
	}
	
	@Override
	protected void updateComponent() {
		componentLabel.setFixedWidth(getBounds().width);
		componentLabel.setFixedHeight(20f);
		componentLabel.setPosition(getBounds().x, getBounds().y + getBounds().height - 15f);
		
		componentDescriptionLabel.setFixedWidth(getBounds().width);
		componentDescriptionLabel.setFixedHeight(70f);
		componentDescriptionLabel.setPosition(getBounds().x, getBounds().y + getBounds().height - 90);
		
		levelUpInfoSection.setFixedWidth(getBounds().width - 20f);
		levelUpInfoSection.setPosition(getBounds().x + 10f, getBounds().y + getBounds().height - 100f - levelUpInfoSection.calculateHeight());
		
		tagLabel.setFixedWidth(getBounds().width);
		minLabel.setFixedWidth(40f);
		maxLabel.setFixedWidth(40f);
		partProgressBar.setFixedWidth(getBounds().width - 90);
		
		if (levelUpButton != null) {
			levelUpButton.setFixedWidth(getBounds().width);
			levelUpButton.setFixedHeight(30f);
			levelUpButton.setPosition(getBounds().x, getBounds().y);
			
			minLabel.setPosition(getBounds().x, getBounds().y + 40f);
			maxLabel.setPosition(getBounds().x + getBounds().width - 40f, getBounds().y + 40f);
			tagLabel.setPosition(getBounds().x, getBounds().y + 10 + 40f);
			partProgressBar.setPosition(getBounds().x + 45, getBounds().y + 40f);
		} else {
			minLabel.setPosition(getBounds().x, getBounds().y);
			maxLabel.setPosition(getBounds().x + getBounds().width - 40f, getBounds().y);
			tagLabel.setPosition(getBounds().x, getBounds().y + 10);
			partProgressBar.setPosition(getBounds().x + 45, getBounds().y);
		}
	}
	
	@Override
	protected void renderComponent() {
		componentLabel.render();
		componentDescriptionLabel.render();
		levelUpInfoSection.render();
		tagLabel.render();
		minLabel.render();
		maxLabel.render();
		partProgressBar.render();
		if (levelUpButton != null) {
			Shapes.draw(EQRectangle.builder()
					.bounds(new Rectangle(getBounds().x + 5f, getBounds().y + 25f + 40f, getBounds().width - 10, 1f))
					.color(adjustedColor)
					.build());
			levelUpButton.render();
		} else {
			Shapes.draw(EQRectangle.builder()
					.bounds(new Rectangle(getBounds().x + 5f, getBounds().y + 25f, getBounds().width - 10, 1f))
					.color(adjustedColor)
					.build());
		}
	}

	@Override
	public float calculateHeight() {
		return 130f + levelUpInfoSection.calculateHeight() + (levelUpButton != null ? 40f : 0f);
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		componentLabel.dispose();
		componentDescriptionLabel.dispose();
		levelUpInfoSection.dispose();
		tagLabel.dispose();
		minLabel.dispose();
		maxLabel.dispose();
		partProgressBar.dispose();
	}

}
