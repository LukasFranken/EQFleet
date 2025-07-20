package de.instinct.eqfleet.menu.module.ship;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.shipyard.dto.ShipBlueprint;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.components.DefaultLabelFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.ship.message.UnuseShipMessage;
import de.instinct.eqfleet.menu.module.ship.message.UseShipMessage;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreview;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class ShipyardRenderer extends BaseModuleRenderer {

	private List<ColorButton> shipButtons;
	
	private final float popupWidth = 200f;
	
	@Override
	public void render() {
		if (ShipyardModel.shipyard != null) {
			renderShipyardInfo();
			if (shipButtons != null) {
				renderShips();
			}
		}
	}

	private void renderShipyardInfo() {
		Rectangle labelBounds = new Rectangle(
				MenuModel.moduleBounds.x + 20,
				MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 30,
				MenuModel.moduleBounds.width - 40,
				20);
		int active = 0;
		for (ShipBlueprint ship : ShipyardModel.shipyard.getOwnedShips()) {
			if (ship.isInUse()) {
				active++;
			}
		}
		Label activeLabel = new Label("Active: " + active + "/" + ShipyardModel.shipyard.getActiveShipSlots());
		activeLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		activeLabel.setBounds(labelBounds);
		activeLabel.render();
	}

	private void renderShips() {
		int elementsPerRow = 5;
		float margin = (((float)MenuModel.moduleBounds.width) - (50 * elementsPerRow)) / ((float)(elementsPerRow + 1));
		
		int i = 0;
		for (ColorButton shipButton : shipButtons) {
			int column = i % elementsPerRow;
			int row = 1 + ((int)i / elementsPerRow);
			shipButton.setPosition(MenuModel.moduleBounds.x + margin + ((50 + margin) * column),
					MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 20 - ((50 + margin) * row));
			shipButton.render();
			i++;
		}
	}

	@Override
	public void reload() {
		shipButtons = new ArrayList<>();
		if (ShipyardModel.shipyard != null && ShipyardModel.shipyard.getOwnedShips() != null) {
			for (ShipBlueprint ship : ShipyardModel.shipyard.getOwnedShips()) {
				if (ship.isBuilt()) shipButtons.add(createShipButton(ship));
			}
		}
	}
	
	private ColorButton createShipButton(ShipBlueprint ship) {
		ColorButton shipButton = DefaultButtonFactory.colorButton(ship.getModel().substring(0, 3), new Action() {
			
			@Override
			public void execute() {
				createShipPopup(ship);
			}
			
		});
		shipButton.setFixedWidth(50);
		shipButton.getBorder().setColor(ship.isInUse() ? Color.GREEN : SkinManager.skinColor);
		return shipButton;
	}
	
	private void createShipPopup(ShipBlueprint ship) {
		ModelInstance shipModel = ModelLoader.instanciate("ship");
        for (Material material : shipModel.materials) {
            material.set(ColorAttribute.createDiffuse(SkinManager.darkestSkinColor));
        }
		ModelPreviewConfiguration shipModelPreviewConfig = ModelPreviewConfiguration.builder()
				.model(shipModel)
				.baseRotationAngle(-90f)
				.baseRotationAxis(new Vector3(1, 0, 0))
				.scale(10f)
				.build();
		ModelPreview shipModelPreview = new ModelPreview(shipModelPreviewConfig);
		Border shipModelPreviewBorder = new Border();
		shipModelPreviewBorder.setSize(2f);
		shipModelPreviewBorder.setColor(Color.GRAY);
		shipModelPreview.setBorder(shipModelPreviewBorder);
		shipModelPreview.setFixedWidth(popupWidth);
		shipModelPreview.setFixedHeight(popupWidth);
		ElementList popupContent = new ElementList();
		popupContent.setMargin(10f);
		popupContent.getElements().add(shipModelPreview);
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("TYPE", ship.getType().toString(), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("COST", ship.getCost() + "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("CP COST", ship.getCommandPointsCost() + "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SPEED", StringUtils.format(ship.getMovementSpeed(), 0) + "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("--------", "--------", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("WEAPON", ship.getWeapon().getType().toString(), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("DAMAGE", StringUtils.format(ship.getWeapon().getDamage(), 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("RANGE", StringUtils.format(ship.getWeapon().getRange(), 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("COOLDOWN", StringUtils.format(ship.getWeapon().getCooldown()/1000f, 1) + "s", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SPEED", StringUtils.format(ship.getWeapon().getSpeed(), 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("--------", "--------", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("DEFENSE", "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("ARMOR", StringUtils.format(ship.getDefense().getArmor(), 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SHIELD", StringUtils.format(ship.getDefense().getShield(), 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SHIELD/SEC", StringUtils.format(ship.getDefense().getShieldRegenerationSpeed(), 1), popupWidth));
		
		ColorButton useButton = DefaultButtonFactory.colorButton(ship.isInUse() ? "Unuse" : "Use", new Action() {
			
			@Override
			public void execute() {
				if (ship.isInUse()) {
					Menu.queue(UnuseShipMessage.builder()
							.shipUUID(ship.getUuid())
							.build());
				} else {
					Menu.queue(UseShipMessage.builder()
							.shipUUID(ship.getUuid())
							.build());
				}
				
				PopupRenderer.close();
			}
			
		});
		useButton.setFixedHeight(30);
		useButton.setFixedWidth(popupWidth);
		popupContent.getElements().add(useButton);
		
		PopupRenderer.create(Popup.builder()
				.title(ship.getModel())
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.build());
	}

	@Override
	public void dispose() {
		if (shipButtons == null) return;
		for (ColorButton shipButton : shipButtons) {
			shipButton.dispose();
		}
	}

}
