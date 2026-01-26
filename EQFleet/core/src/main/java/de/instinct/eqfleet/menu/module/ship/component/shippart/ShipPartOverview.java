package de.instinct.eqfleet.menu.module.ship.component.shippart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.shipyard.dto.ship.PlayerShipComponentLevel;
import de.instinct.api.shipyard.dto.ship.ShipComponent;
import de.instinct.api.shipyard.dto.ship.component.ComponentAttribute;
import de.instinct.api.shipyard.dto.ship.component.ComponentLevel;
import de.instinct.api.shipyard.service.impl.ShipyardUtility;
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
	private PlayerShipComponentLevel shipComponentLevel;
	private ShipComponent component;
	
	private ShipPartLevelArea levelArea;
	private List<ElementStack> infoElements;
	
	public ShipPartOverview(float width, PlayerShipComponentLevel shipComponentLevel, ShipComponent component) {
		super();
		this.width = width;
		this.shipComponentLevel = shipComponentLevel;
		this.component = component;
		this.partType = ShipPartType.valueOf(ShipyardUtility.getShipComponentType(component).toUpperCase());
		
		createLevelArea();
		initializeInfoElements();
	}

	private void createLevelArea() {
		ComponentLevel currentLevel = null;
		ComponentLevel nextLevel = null;
		
		for (ComponentLevel componentLevel : component.getLevels()) {
			if (componentLevel.getLevel() == shipComponentLevel.getLevel()) {
				currentLevel = componentLevel;
			}
			if (componentLevel.getLevel() == shipComponentLevel.getLevel() + 1) {
				nextLevel = componentLevel;
			}
		}
		
		levelArea = new ShipPartLevelArea(shipComponentLevel.getLevel(), getPartTypeColor(), shipComponentLevel.getProgress(), nextLevel == null ? currentLevel.getRequirementValue() : nextLevel.getRequirementValue());
		levelArea.getColorButton().setLayer(1);
		levelArea.getColorButton().setGlowAnimation(false);
		levelArea.getColorButton().setAction(new Action() {
			
			@Override
			public void execute() {
				createPartLevelPopup();
			}
			
		});
	}

	private void initializeInfoElements() {
		infoElements = new ArrayList<>();
		
		for (ComponentLevel componentLevel : component.getLevels()) {
			if (componentLevel.getLevel() == shipComponentLevel.getLevel()) {
				for (ComponentAttribute attribute : componentLevel.getAttributes()) {
					ElementStack infoElement = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
							.type(FontType.SMALL)
							.tag(ShipyardUtility.getAttributeName(attribute).replaceAll("_", " "))
							.value(StringUtils.format(attribute.getValue(), 1))
							.width(width - 85)
							.colorTag(getPartTypeColor())
							.colorValue(getPartTypeColor())
							.build());
					infoElements.add(infoElement);
				}
			}
		}
	}

	private void createPartLevelPopup() {
		ElementList popupContent = new ElementList();
		popupContent.setMargin(10f);
		
		Color partColor = getPartTypeColor();
		ComponentLevel currentLevel = null;
		ComponentLevel nextLevel = null;
		
		for (ComponentLevel componentLevel : component.getLevels()) {
			if (componentLevel.getLevel() == shipComponentLevel.getLevel()) {
				currentLevel = componentLevel;
			}
			if (componentLevel.getLevel() == shipComponentLevel.getLevel() + 1) {
				nextLevel = componentLevel;
			}
		}
		
		if (currentLevel != null) {
			ShipPartLevelOverviewArea partLevelOverviewArea = 
					new ShipPartLevelOverviewArea(ShipPartLevelOverviewAreaConfig.builder()
							.tag(ShipyardUtility.getComponentLevelType(currentLevel).replaceAll("_", " "))
							.partColor(partColor)
							.componentType(ShipyardUtility.getShipComponentSubtype(component).replaceAll("_", " "))
							.componentDescription(ShipSubtypeDescription.get(ShipyardUtility.getShipComponentSubtype(component)))
							.currentValue(shipComponentLevel.getProgress())
							.minValue(currentLevel.getRequirementValue())
							.maxValue(nextLevel != null ? nextLevel.getRequirementValue() : -1)
							.infoSectionConfig(LevelUpInfoSectionConfig.builder()
									.currentLevel(currentLevel.getLevel())
									.nextLevel(nextLevel != null ? nextLevel.getLevel() : -1)
									.levelUpInfos(getLevelUpInfos(currentLevel, nextLevel))
									.color(partColor)
									.build())
							.build());
			
			partLevelOverviewArea.setFixedWidth(width + 50f);
			popupContent.getElements().add(partLevelOverviewArea);
		}
		
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

	private List<LevelUpInfo> getLevelUpInfos(ComponentLevel currentLevel, ComponentLevel nextLevel) {
		List<LevelUpInfo> levelUpInfos = new ArrayList<>();
		Map<Integer, ComponentAttribute> currentAttrs = new HashMap<>();
		if (currentLevel != null) {
			for (ComponentAttribute attr : currentLevel.getAttributes()) {
				currentAttrs.put(attr.getId(), attr);
		    }
		}

		Map<Integer, ComponentAttribute> nextAttrs = new HashMap<>();
		if (nextLevel != null) {
			for (ComponentAttribute attr : nextLevel.getAttributes()) {
				nextAttrs.put(attr.getId(), attr);
			}
		}

		Set<Integer> allIds = new HashSet<>(currentAttrs.keySet());
		allIds.addAll(nextAttrs.keySet());

		for (Integer id : allIds) {
			ComponentAttribute currAttr = currentAttrs.get(id);
			ComponentAttribute nextAttr = nextAttrs.get(id);
			ComponentAttribute attrForName = currAttr != null ? currAttr : nextAttr;

			double currentValue = currAttr != null ? currAttr.getValue() : 0;
			double nextValue = nextAttr != null ? nextAttr.getValue() : 0;

		 	levelUpInfos.add(LevelUpInfo.builder()
		                .tagValue(ShipyardUtility.getAttributeName(attrForName).replaceAll("_", " "))
		                .currentValue(StringUtils.format(currentValue, 1))
		                .changeValue(nextAttr != null ? (nextValue - currentValue > 0 ? "+" : "") + StringUtils.format(nextValue - currentValue, 1) : "")
		                .nextValue(nextAttr != null ? StringUtils.format(nextValue, 1) : "")
		                .build());
		}
		return levelUpInfos;
	}

	@Override
	protected void updateComponent() {
		levelArea.setBounds(new Rectangle(getBounds().x + 10, getBounds().y + 10, 45, 45));
		
		int i = 0;
		for (ElementStack infoElement : infoElements) {
			infoElement.setPosition(getBounds().x + 75, getBounds().y + 60 - (i * 13));
			i++;
		}
	}
	
	@Override
	protected void renderComponent() {
		Shapes.draw(EQRectangle.builder()
				.bounds(getBounds())
				.color(getPartTypeColor())
				.label(partType.name().toUpperCase())
				.build());
		
		Label partTypeLabel = new Label(ShipyardUtility.getShipComponentSubtype(component).toUpperCase());
		partTypeLabel.setType(FontType.TINY);
		partTypeLabel.setColor(getPartTypeColor());
		partTypeLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		partTypeLabel.setBounds(new Rectangle(getBounds().x, getBounds().y + 60, 65, 10));
		partTypeLabel.render();
		
		levelArea.render();
		
		TextureManager.draw(TextureManager.createTexture(getPartTypeColor()), new Rectangle(getBounds().x + 65, getBounds().y + 5, 1, getBounds().height - 30), 0.5f);
		
		for (ElementStack infoElement : infoElements) {
			infoElement.render();
		}
	}

	private Color getPartTypeColor() {
		switch (partType) {
			case CORE:
				return Color.PURPLE;
			case ENGINE:
				return Color.YELLOW;
			case HULL:
				return Color.ORANGE;
			case SHIELD:
				return Color.CYAN;
			case WEAPON:
				return Color.RED;
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
		levelArea.dispose();
	}

}
