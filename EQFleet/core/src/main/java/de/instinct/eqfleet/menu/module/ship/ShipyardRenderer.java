package de.instinct.eqfleet.menu.module.ship;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.game.engine.EngineInterface;
import de.instinct.api.shipyard.dto.PlayerShipData;
import de.instinct.engine.model.ship.ShipData;
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
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.LabeledModelButton;
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

	private List<LabeledModelButton> shipButtons;
	
	private final float popupWidth = 250f;
	
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
		for (PlayerShipData ship : ShipyardModel.playerShipyard.getShips()) {
			if (ship.isInUse()) {
				active++;
			}
		}
		Label activeLabel = new Label("Active: " + active + "/" + ShipyardModel.playerShipyard.getActiveShipSlots());
		activeLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		activeLabel.setBounds(labelBounds);
		activeLabel.render();
	}

	private void renderShips() {
		int elementsPerRow = 5;
		float margin = (((float)MenuModel.moduleBounds.width) - (50 * elementsPerRow)) / ((float)(elementsPerRow + 1));
		
		int i = 0;
		for (LabeledModelButton shipButton : shipButtons) {
			int column = i % elementsPerRow;
			int row = 1 + ((int)i / elementsPerRow);
			shipButton.setPosition(MenuModel.moduleBounds.x + margin + ((50 + margin) * column),
					MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 20 - ((70 + margin) * row));
			shipButton.render();
			i++;
		}
	}

	@Override
	public void reload() {
		shipButtons = new ArrayList<>();
		if (ShipyardModel.shipyard != null && ShipyardModel.playerShipyard.getShips() != null) {
			for (PlayerShipData playerShip : ShipyardModel.playerShipyard.getShips()) {
				if (playerShip.isBuilt()) {
					ShipData shipData  = EngineInterface.getShipData(playerShip, ShipyardModel.shipyard);
					shipButtons.add(createShipButton(playerShip, shipData));
				}
			}
		}
	}
	
	private LabeledModelButton createShipButton(PlayerShipData playerShip, ShipData shipData) {
		ModelInstance model = ModelLoader.instanciate("ship");
        for (Material material : model.materials) {
            material.set(ColorAttribute.createDiffuse(SkinManager.darkestSkinColor));
        }
        
		ModelPreviewConfiguration shipModelPreviewConfig = ModelPreviewConfiguration.builder()
				.model(model)
				.baseRotationAngle(-90f)
				.baseRotationAxis(new Vector3(1, 0, 0))
				.scale(20f)
				.grid(false)
				.build();
		
		LabeledModelButton shipButton = new LabeledModelButton(shipModelPreviewConfig, shipData.model.toUpperCase(), new Action() {
			
			@Override
			public void execute() {
				createShipPopup(playerShip, shipData);
			}
			
		});
		shipButton.setFixedWidth(50f);
		shipButton.setFixedHeight(70f);
		shipButton.setNoteLabel("Lv " + playerShip.getLevel(), Color.GRAY);
		shipButton.getModelPreview().getBorder().setColor(playerShip.isInUse() ? Color.GREEN : SkinManager.skinColor);
		return shipButton;
	}
	
	private void createShipPopup(PlayerShipData playerShip, ShipData shipData) {
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
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("TYPE", shipData.type.toString(), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("RES. COST", shipData.cost + "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("CP COST", shipData.commandPointsCost + "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SPEED", StringUtils.format(shipData.movementSpeed, 0) + "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("----------", "----------", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("WEAPON", shipData.weapon.type.toString(), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("DAMAGE", StringUtils.format(shipData.weapon.damage, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("RANGE", StringUtils.format(shipData.weapon.range, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("COOLDOWN", StringUtils.format(shipData.weapon.cooldown/1000f, 1) + "s", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SPEED", StringUtils.format(shipData.weapon.speed, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("----------", "----------", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("DEFENSE", "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("ARMOR", StringUtils.format(shipData.defense.armor, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SHIELD", StringUtils.format(shipData.defense.shield, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SH. REG. /s", StringUtils.format(shipData.defense.shieldRegenerationSpeed, 1), popupWidth));
		
		ColorButton useButton = DefaultButtonFactory.colorButton(playerShip.isInUse() ? "Unuse" : "Use", new Action() {
			
			@Override
			public void execute() {
				if (playerShip.isInUse()) {
					Menu.queue(UnuseShipMessage.builder()
							.shipUUID(playerShip.getUuid())
							.build());
				} else {
					Menu.queue(UseShipMessage.builder()
							.shipUUID(playerShip.getUuid())
							.build());
				}
				
				PopupRenderer.close();
			}
			
		});
		useButton.setFixedHeight(30);
		useButton.setFixedWidth(popupWidth);
		popupContent.getElements().add(useButton);
		
		PopupRenderer.create(Popup.builder()
				.title(shipData.model + " - Lv. " + playerShip.getLevel())
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.build());
	}

	@Override
	public void dispose() {
		if (shipButtons == null) return;
		for (LabeledModelButton shipButton : shipButtons) {
			shipButton.dispose();
		}
	}

}
