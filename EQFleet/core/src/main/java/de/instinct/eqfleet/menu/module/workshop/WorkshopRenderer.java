package de.instinct.eqfleet.menu.module.workshop;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.api.shipyard.dto.PlayerShipData;
import de.instinct.api.shipyard.dto.ShipBlueprint;
import de.instinct.api.shipyard.dto.ShipBuildResponse;
import de.instinct.api.shipyard.dto.ShipLevel;
import de.instinct.api.shipyard.dto.ShipStatChange;
import de.instinct.api.shipyard.dto.ShipUpgradeResponse;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.components.DefaultLabelFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.ship.ShipyardModel;
import de.instinct.eqfleet.menu.module.workshop.message.BuildShipMessage;
import de.instinct.eqfleet.menu.module.workshop.message.UpgradeShipMessage;
import de.instinct.eqfleet.menu.module.workshop.model.BundledShipData;
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

public class WorkshopRenderer extends BaseModuleRenderer {
	
	private Label spaceLabel;
	private List<LabeledModelButton> shipButtons;
	
	private final float popupWidth = 250f;

	@Override
	public void render() {
		if (ShipyardModel.shipyard != null) {
			renderWorkshopInfo();
			if (shipButtons != null) {
				renderShips();
			}
		}
	}

	private void renderWorkshopInfo() {
		spaceLabel.render();
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
		Rectangle labelBounds = new Rectangle(
				MenuModel.moduleBounds.x + 20,
				MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 30,
				MenuModel.moduleBounds.width - 40,
				20);
		spaceLabel = new Label("Hangar Space: " + ShipyardModel.playerShipyard.getUsedSlots() + "/" + ShipyardModel.playerShipyard.getSlots());
		spaceLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		spaceLabel.setBounds(labelBounds);
		createShipButtons();
		checkForResponses();
	}
	
	private void checkForResponses() {
		if (WorkshopModel.shipBuildResponse != null) {
			if (WorkshopModel.shipBuildResponse != ShipBuildResponse.SUCCESS) PopupRenderer.createMessageDialog("Ship build failed", WorkshopModel.shipBuildResponse.toString().replace("_", " "));
			WorkshopModel.shipBuildResponse = null;
		}
		if (WorkshopModel.shipUpgradeResponse != null) {
			if (WorkshopModel.shipUpgradeResponse != ShipUpgradeResponse.SUCCESS) PopupRenderer.createMessageDialog("Ship upgrade failed", WorkshopModel.shipUpgradeResponse.toString().replace("_", " "));
			WorkshopModel.shipUpgradeResponse = null;
		}
	}

	private void createShipButtons() {
		shipButtons = new ArrayList<>();
		if (ShipyardModel.shipyard != null && ShipyardModel.playerShipyard != null) {
			for (PlayerShipData playerShip : ShipyardModel.playerShipyard.getShips()) {
				BundledShipData bundledShipData = BundledShipData.builder()
						.playerShipData(playerShip)
						.blueprint(getBlueprint(playerShip.getShipId()))
						.build();
				if (bundledShipData.getBlueprint().getLevels().size() > playerShip.getLevel()) shipButtons.add(createShipButton(bundledShipData));
			}
		}
	}
	
	private ShipBlueprint getBlueprint(int shipId) {
		for (ShipBlueprint shipBlueprint : ShipyardModel.shipyard.getShipBlueprints()) {
			if (shipBlueprint.getId() == shipId) {
				return shipBlueprint;
			}
		}
		return null;
	}

	private LabeledModelButton createShipButton(BundledShipData bundledShipData) {
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
		
		LabeledModelButton shipButton = new LabeledModelButton(shipModelPreviewConfig, bundledShipData.getBlueprint().getModel().toUpperCase(), new Action() {
			
			@Override
			public void execute() {
				if (bundledShipData.getPlayerShipData().isBuilt()) {
					createShipUpgradePopup(bundledShipData);
				} else {
					createShipBuildPopup(bundledShipData);
				}
			}
			
		});
		shipButton.setFixedWidth(50f);
		shipButton.setFixedHeight(70f);
		shipButton.setNoteLabel("Lv " + bundledShipData.getPlayerShipData().getLevel(), Color.GRAY);
		shipButton.getModelPreview().getBorder().setColor(bundledShipData.getPlayerShipData().isBuilt() ? SkinManager.skinColor : Color.BLUE);
		return shipButton;
	}
	
	private void createShipUpgradePopup(BundledShipData bundledShipData) {
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
		
		int currentLevel = bundledShipData.getPlayerShipData().getLevel();
		ShipLevel level = bundledShipData.getBlueprint().getLevels().get(currentLevel);
		if (level != null) {
			popupContent.getElements().add(DefaultLabelFactory.createLabelStack("Lv. " + currentLevel, "->      Lv. " + (currentLevel + 1), popupWidth));
			popupContent.getElements().add(DefaultLabelFactory.createLabelStack(" ", " ", popupWidth));
			popupContent.getElements().add(DefaultLabelFactory.createLabelStack("EFFECTS", "----------", popupWidth));
			for (ShipStatChange statChange : level.getStatEffects()) {
				popupContent.getElements().add(DefaultLabelFactory.createLabelStack(statChange.getStat().getLabel(), (statChange.getValue() > 0 ? "+" : "") + statChange.getValue(), popupWidth));
			}
			popupContent.getElements().add(DefaultLabelFactory.createLabelStack(" ", " ", popupWidth));
			popupContent.getElements().add(DefaultLabelFactory.createLabelStack("COST", "----------", popupWidth));
			for (ResourceAmount cost : level.getCost()) {
				popupContent.getElements().add(DefaultLabelFactory.createCostStack(cost, popupWidth));
			}
		}
		
		ColorButton upgradeButton = DefaultButtonFactory.colorButton("Upgrade", new Action() {
			
			@Override
			public void execute() {
				Menu.queue(UpgradeShipMessage.builder()
						.shipUUID(bundledShipData.getPlayerShipData().getUuid())
						.build());
				PopupRenderer.close();
			}
			
		});
		upgradeButton.setFixedHeight(30);
		upgradeButton.setFixedWidth(popupWidth);
		popupContent.getElements().add(upgradeButton);
		
		PopupRenderer.create(Popup.builder()
				.title(bundledShipData.getBlueprint().getModel() + " - Lv. " + bundledShipData.getPlayerShipData().getLevel())
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.build());
	}
	
	private void createShipBuildPopup(BundledShipData bundledShipData) {
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
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("BUILD COST", "----------", popupWidth));
		for (ResourceAmount cost : bundledShipData.getBlueprint().getBuildCost()) {
			popupContent.getElements().add(DefaultLabelFactory.createCostStack(cost, popupWidth));
		}
		ColorButton buildButton = DefaultButtonFactory.colorButton("Build", new Action() {
			
			@Override
			public void execute() {
				Menu.queue(BuildShipMessage.builder()
						.shipUUID(bundledShipData.getPlayerShipData().getUuid())
						.build());
				PopupRenderer.close();
			}
			
		});
		buildButton.setFixedHeight(30);
		buildButton.setFixedWidth(popupWidth);
		popupContent.getElements().add(buildButton);
		
		PopupRenderer.create(Popup.builder()
				.title(bundledShipData.getBlueprint().getModel())
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
