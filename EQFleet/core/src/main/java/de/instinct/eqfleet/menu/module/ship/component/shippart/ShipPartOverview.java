package de.instinct.eqfleet.menu.module.ship.component.shippart;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.shipyard.dto.PlayerShipData;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.eqfleet.menu.common.components.label.DefaultLabelFactory;
import de.instinct.eqfleet.menu.common.components.label.LabelStackConfiguration;
import de.instinct.eqfleet.menu.module.ship.component.shippart.level.ShipPartLevelArea;
import de.instinct.eqfleet.menu.module.ship.component.shippart.level.ShipPartLevelOverviewArea;
import de.instinct.eqfleet.menu.module.ship.component.shippart.level.config.ShipPartLevelOverviewAreaConfig;
import de.instinct.eqfleet.menu.module.ship.component.shippart.level.levelupinfo.LevelUpInfo;
import de.instinct.eqfleet.menu.module.ship.component.shippart.level.levelupinfo.LevelUpInfoSectionConfig;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShipPartOverview extends Component {
	
	private float width;
	private ShipPartType partType;
	private PlayerShipData playerShip;
	private ShipData shipData;
	
	private ShipPartLevelArea levelArea;
	
	public ShipPartOverview(float width, ShipPartType partType, PlayerShipData playerShip, ShipData shipData) {
		super();
		this.width = width;
		this.partType = partType;
		this.partType = partType;
		this.playerShip = playerShip;
		this.shipData = shipData;
		
		levelArea = new ShipPartLevelArea(2, getPartTypeColor(), 50, 100);
		levelArea.getColorButton().setLayer(1);
		levelArea.getColorButton().setGlowAnimation(true);
		levelArea.getColorButton().setAction(new Action() {
			
			@Override
			public void execute() {
				createPartLevelPopup();
			}
			
		});
	}
	
	private void createPartLevelPopup() {
		ElementList popupContent = new ElementList();
		popupContent.setMargin(10f);
		
		Color partColor = getPartTypeColor();
		
		List<LevelUpInfo> levelUpInfos = new ArrayList<>();
		levelUpInfos.add(LevelUpInfo.builder()
				.tagValue("Damage")
				.currentValue("10")
				.changeValue("+2")
				.nextValue("12")
				.build());
		levelUpInfos.add(LevelUpInfo.builder()
				.tagValue("Range")
				.currentValue("10")
				.changeValue("+2")
				.nextValue("12")
				.build());
		levelUpInfos.add(LevelUpInfo.builder()
				.tagValue("Speed")
				.currentValue("10")
				.changeValue("+2")
				.nextValue("12")
				.build());
		levelUpInfos.add(LevelUpInfo.builder()
				.tagValue("AOE")
				.currentValue("10")
				.changeValue("+2")
				.nextValue("12")
				.build());
		
		ShipPartLevelOverviewArea partLevelOverviewArea = 
				new ShipPartLevelOverviewArea(ShipPartLevelOverviewAreaConfig.builder()
						.tag("KILLS")
						.partColor(partColor)
						.componentType(getComponentType())
						.componentDescription("While lacking in firepower\nand durability, fighters\nare cheap and typically\nfaster than larger ships.")
						.currentValue(50)
						.maxValue(100)
						.minValue(0)
						.minValueLabel("0")
						.maxValueLabel("100")
						.infoSectionConfig(LevelUpInfoSectionConfig.builder()
								.currentLevel(2)
								.nextLevel(3)
								.levelUpInfos(levelUpInfos)
								.color(partColor)
								.build())
						.levelUpAction(new Action() {
							
							@Override
							public void execute() {
								
							}
							
						})
						.build());
		
		partLevelOverviewArea.setFixedWidth(200f);
		popupContent.getElements().add(partLevelOverviewArea);
		
		Color windowColor = new Color(partColor);
		windowColor.r *= 0.5f;
		windowColor.g *= 0.5f;
		windowColor.b *= 0.5f;
		PopupRenderer.create(Popup.builder()
				.title(partType.name().toUpperCase())
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.windowColor(windowColor)
				.titleColor(partColor)
				.build());
	}

	@Override
	protected void updateComponent() {
		levelArea.setBounds(new Rectangle(getBounds().x + 10, getBounds().y + 10, 45, 45));
	}
	
	@Override
	protected void renderComponent() {
		Shapes.draw(EQRectangle.builder()
				.bounds(getBounds())
				.color(getPartTypeColor())
				.label(partType.name().toUpperCase())
				.build());
		
		Label partTypeLabel = new Label(getComponentType());
		partTypeLabel.setType(FontType.TINY);
		partTypeLabel.setColor(getPartTypeColor());
		partTypeLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		partTypeLabel.setBounds(new Rectangle(getBounds().x, getBounds().y + 60, 65, 10));
		partTypeLabel.render();
		
		levelArea.render();
		
		TextureManager.draw(TextureManager.createTexture(getPartTypeColor()), new Rectangle(getBounds().x + 65, getBounds().y + 5, 1, getBounds().height - 30), 0.5f);
		
		switch (partType) {
			case CORE:
				ElementStack cpCostLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("CP Cost")
						.value(StringUtils.format(3, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				cpCostLabelStack.setPosition(getBounds().x + 75, getBounds().y + 60);
				cpCostLabelStack.render();
				
				ElementStack costLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Resource Cost")
						.value(StringUtils.format(7, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				costLabelStack.setPosition(getBounds().x + 75, getBounds().y + 47);
				costLabelStack.render();
				break;
			case WEAPON:
				ElementStack damageLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Damage")
						.value(StringUtils.format(10, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				damageLabelStack.setPosition(getBounds().x + 75, getBounds().y + 60);
				damageLabelStack.render();
				
				ElementStack rangeLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Range")
						.value(StringUtils.format(200, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				rangeLabelStack.setPosition(getBounds().x + 75, getBounds().y + 47);
				rangeLabelStack.render();
				
				ElementStack cooldownLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Cooldown")
						.value(StringUtils.format(0.5, 1) + "s")
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				cooldownLabelStack.setPosition(getBounds().x + 75, getBounds().y + 34);
				cooldownLabelStack.render();
				
				ElementStack speedLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Speed")
						.value(StringUtils.format(100, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				speedLabelStack.setPosition(getBounds().x + 75, getBounds().y + 21);
				speedLabelStack.render();
				
				ElementStack aoeLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Explosion")
						.value(StringUtils.format(50, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				aoeLabelStack.setPosition(getBounds().x + 75, getBounds().y + 9);
				aoeLabelStack.render();
				break;
			case SHIELD:
				ElementStack shieldHitpointsLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Strength")
						.value(StringUtils.format(20, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				shieldHitpointsLabelStack.setPosition(getBounds().x + 75, getBounds().y + 60);
				shieldHitpointsLabelStack.render();
				
				ElementStack regenerationLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Generation")
						.value(StringUtils.format(2.5, 1) + "/s")
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				regenerationLabelStack.setPosition(getBounds().x + 75, getBounds().y + 47);
				regenerationLabelStack.render();
				break;
			case HULL:
				ElementStack hullHitpointsLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Strength")
						.value(StringUtils.format(10, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				hullHitpointsLabelStack.setPosition(getBounds().x + 75, getBounds().y + 60);
				hullHitpointsLabelStack.render();
				break;
			case ENGINE:
				ElementStack engineSpeedLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Speed")
						.value(StringUtils.format(100, 0))
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				engineSpeedLabelStack.setPosition(getBounds().x + 75, getBounds().y + 60);
				engineSpeedLabelStack.render();
				
				ElementStack accelerationLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
						.type(FontType.SMALL)
						.tag("Acceleration")
						.value(StringUtils.format(5, 0) + "/s")
						.width(getBounds().width - 85)
						.colorTag(getPartTypeColor())
						.colorValue(getPartTypeColor())
						.build());
				accelerationLabelStack.setPosition(getBounds().x + 75, getBounds().y + 47);
				accelerationLabelStack.render();
				break;
		}
	}

	private String getComponentType() {
		switch (partType) {
			case CORE:
				return shipData.type.name().toUpperCase();
			case WEAPON:
				return shipData.weapon.type.name().toUpperCase();
			case SHIELD:
				return "GRAVITON";
			case HULL:
				return "CARBON";
			case ENGINE:
				return "ION";
	}
		return "";
	}

	private Color getPartTypeColor() {
		switch (partType) {
			case CORE:
				return Color.PURPLE;
			case WEAPON:
				return Color.RED;
			case SHIELD:
				return Color.CYAN;
			case HULL:
				return Color.ORANGE;
			case ENGINE:
				return Color.YELLOW;
		}
		return Color.GRAY;
	}

	@Override
	public float calculateHeight() {
		return 95;
	}

	@Override
	public float calculateWidth() {
		return width;
	}

	@Override
	public void dispose() {
		
	}

}
