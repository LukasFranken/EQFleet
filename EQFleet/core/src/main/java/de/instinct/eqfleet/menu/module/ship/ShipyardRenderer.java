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
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.ship.component.shippart.ShipPartOverview;
import de.instinct.eqfleet.menu.module.ship.component.shippart.ShipPartType;
import de.instinct.eqfleet.menu.module.ship.message.UnuseShipMessage;
import de.instinct.eqfleet.menu.module.ship.message.UseShipMessage;
import de.instinct.eqfleet.menu.module.workshop.message.BuildShipMessage;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.Button;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.LabeledModelButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreview;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class ShipyardRenderer extends BaseModuleRenderer {

	private List<Button> activeShipButtons;
	private List<Button> shipButtons;
	private Label spaceLabel;
	private Label activeLabel;
	
	private final float popupWidth = 200f;
	
	private Color blueprintColor = new Color(0f, 0f, 0.5f, 1f);
	
	@Override
	public void render() {
		if (ShipyardModel.shipyard != null) {
			spaceLabel.render();
			activeLabel.render();
			if (shipButtons != null) {
				renderUsedShips();
				TextureManager.draw(TextureManager.createTexture(new Color(1f, 1f, 1f, 0.2f)), 
						GraphicsUtil.scaleFactorAdjusted(new Rectangle(
								MenuModel.moduleBounds.x + 20,
								MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 120,
								MenuModel.moduleBounds.width - 40,
								1)));
				renderUnusedShips();
			}
		}
	}

	private void renderUsedShips() {
		int elementsPerRow = 5;
		float margin = (((float)MenuModel.moduleBounds.width) - (50 * elementsPerRow)) / ((float)(elementsPerRow + 1));
		
		int i = 0;
		for (Button shipButton : activeShipButtons) {
			int column = i % elementsPerRow;
			shipButton.setPosition(MenuModel.moduleBounds.x + margin + ((50 + margin) * column),
					MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 20 - (((shipButton instanceof ColorButton ? 50 : 70) + margin)));
			shipButton.render();
			i++;
		}
	}

	private void renderUnusedShips() {
		int elementsPerRow = 5;
		float margin = (((float)MenuModel.moduleBounds.width) - (50 * elementsPerRow)) / ((float)(elementsPerRow + 1));
		
		int i = 0;
		for (Button shipButton : shipButtons) {
			int column = i % elementsPerRow;
			int row = 1 + ((int)i / elementsPerRow);
			shipButton.setPosition(MenuModel.moduleBounds.x + margin + ((50 + margin) * column),
					MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 100 - 20 - ((70 + margin) * row));
			shipButton.render();
			i++;
		}
	}

	@Override
	public void reload() {
		if (ShipyardModel.playerShipyard != null) {
			Rectangle activeLabelBounds = new Rectangle(
					MenuModel.moduleBounds.x + 10,
					MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 25,
					MenuModel.moduleBounds.width - 20,
					20);
			int active = 0;
			for (PlayerShipData ship : ShipyardModel.playerShipyard.getShips()) {
				if (ship.isInUse()) {
					active++;
				}
			}
			activeLabel = new Label("Active: " + active + "/" + ShipyardModel.playerShipyard.getActiveShipSlots());
			activeLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
			activeLabel.setBounds(activeLabelBounds);
			activeLabel.setType(FontType.SMALL);
			
			spaceLabel = new Label("Space: " + ShipyardModel.playerShipyard.getUsedSlots() + "/" + ShipyardModel.playerShipyard.getSlots());
			spaceLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			spaceLabel.setBounds(activeLabelBounds);
			spaceLabel.setType(FontType.SMALL);
			
			activeShipButtons = new ArrayList<>();
			shipButtons = new ArrayList<>();
			if (ShipyardModel.shipyard != null && ShipyardModel.playerShipyard.getShips() != null) {
				for (PlayerShipData playerShip : ShipyardModel.playerShipyard.getShips()) {
					ShipData shipData  = EngineInterface.getShipData(playerShip, ShipyardModel.shipyard);
					if (playerShip.isInUse()) {
						activeShipButtons.add(createShipButton(playerShip, shipData));
					} else {
						if (playerShip.isBuilt()) {
							shipButtons.add(createShipButton(playerShip, shipData));
						} else {
							shipButtons.add(createUnbuiltShipButton(playerShip, shipData));
						}
					}
				}
				while (activeShipButtons.size() < ShipyardModel.playerShipyard.getActiveShipSlots()) {
					activeShipButtons.add(createUnusedActiveShipButton());
				}
			}
		}
	}
	
	private Button createUnusedActiveShipButton() {
		ColorButton button = DefaultButtonFactory.colorButton("+", null);
		button.getBorder().setColor(Color.DARK_GRAY);
		button.setFixedWidth(50f);
		button.setFixedHeight(50f);
		button.getLabel().setColor(Color.DARK_GRAY);
		button.getLabel().setType(FontType.GIANT);
		button.getLabel().setHorizontalAlignment(HorizontalAlignment.CENTER);
		button.setEnabled(false);
		return button;
	}

	private LabeledModelButton createShipButton(PlayerShipData playerShip, ShipData shipData) {
		ModelInstance model = null;
		model = ModelLoader.instanciate("ship");
        for (Material material : model.materials) {
        	material.set(ColorAttribute.createDiffuse(SkinManager.darkestSkinColor));
        }
        
		ModelPreviewConfiguration shipModelPreviewConfig = ModelPreviewConfiguration.builder()
				.model(model)
				.baseRotationAngle(-90f)
				.baseRotationAxis(new Vector3(1, 0, 0))
				.scale(20f)
				.build();
		
		LabeledModelButton shipButton = new LabeledModelButton(shipModelPreviewConfig, shipData.model.toUpperCase(), new Action() {
			
			@Override
			public void execute() {
				createShipPopup(playerShip, shipData);
			}
			
		});
		shipButton.setFixedWidth(50f);
		shipButton.setFixedHeight(70f);
		shipButton.getModelPreview().getBorder().setColor(SkinManager.skinColor);
		shipButton.setNoteLabel("Lv " + playerShip.getLevel(), Color.GRAY);
		return shipButton;
	}
	
	private LabeledModelButton createUnbuiltShipButton(PlayerShipData playerShip, ShipData shipData) {
		ModelPreviewConfiguration shipModelPreviewConfig = ModelPreviewConfiguration.builder()
				.gridConfig(GridConfiguration.builder()
						.step(5f)
						.lineThickness(1f)
						.lineColor(new Color(0f, 0f, 0.3f, 0.7f))
						.build())
				.build();
		
		LabeledModelButton shipButton = new LabeledModelButton(shipModelPreviewConfig, shipData.model.toUpperCase(), new Action() {
			
			@Override
			public void execute() {
				createShipPopup(playerShip, shipData);
			}
			
		});
		shipButton.setFixedWidth(50f);
		shipButton.setFixedHeight(70f);
		shipButton.getModelPreview().getBorder().setColor(blueprintColor);
		shipButton.setHoverTexture(TextureManager.createTexture(blueprintColor));
		shipButton.getLabel().setColor(blueprintColor);
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
		shipModelPreview.setFixedHeight(150);
		ElementList popupContent = new ElementList();
		popupContent.setMargin(10f);
		popupContent.getElements().add(shipModelPreview);
		/*popupContent.getElements().add(DefaultLabelFactory.createLabelStack("TYPE", shipData.type.toString(), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("RES. COST", shipData.cost + "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("CP COST", shipData.commandPointsCost + "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SPEED", StringUtils.format(shipData.movementSpeed, 0) + "", popupWidth));*/
		ShipPartOverview coreOverview = new ShipPartOverview(popupWidth, ShipPartType.CORE, playerShip, shipData);
		popupContent.getElements().add(coreOverview);
		/*popupContent.getElements().add(DefaultLabelFactory.createLabelStack("WEAPON", shipData.weapon.type.toString(), popupWidth));
		if (shipData.weapon.aoeRadius > 0) {
			popupContent.getElements().add(DefaultLabelFactory.createLabelStack("AOE RADIUS", StringUtils.format(shipData.weapon.aoeRadius, 0), popupWidth));
		}
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("DAMAGE", StringUtils.format(shipData.weapon.damage, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("RANGE", StringUtils.format(shipData.weapon.range, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("COOLDOWN", StringUtils.format(shipData.weapon.cooldown/1000f, 1) + "s", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SPEED", StringUtils.format(shipData.weapon.speed, 0), popupWidth));*/
		ShipPartOverview weaponOverview = new ShipPartOverview(popupWidth, ShipPartType.WEAPON, playerShip, shipData);
		popupContent.getElements().add(weaponOverview);
		/*popupContent.getElements().add(DefaultLabelFactory.createLabelStack("DEFENSE", "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("ARMOR", StringUtils.format(shipData.defense.armor, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SHIELD", StringUtils.format(shipData.defense.shield, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SH. REG. /s", StringUtils.format(shipData.defense.shieldRegenerationSpeed, 1), popupWidth));*/
		ShipPartOverview shieldOverview = new ShipPartOverview(popupWidth, ShipPartType.SHIELD, playerShip, shipData);
		popupContent.getElements().add(shieldOverview);
		
		ShipPartOverview hullOverview = new ShipPartOverview(popupWidth, ShipPartType.HULL, playerShip, shipData);
		popupContent.getElements().add(hullOverview);
		
		ShipPartOverview engineOverview = new ShipPartOverview(popupWidth, ShipPartType.ENGINE, playerShip, shipData);
		popupContent.getElements().add(engineOverview);
		
		if (playerShip.isBuilt()) {
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
			useButton.setLayer(1);
			popupContent.getElements().add(useButton);
		} else {
			ColorButton buildButton = DefaultButtonFactory.colorButton("Build", new Action() {
				
				@Override
				public void execute() {
					Menu.queue(BuildShipMessage.builder()
							.shipUUID(playerShip.getUuid())
							.build());
					PopupRenderer.close();
				}
				
			});
			buildButton.setFixedHeight(30);
			buildButton.setFixedWidth(popupWidth);
			buildButton.setLayer(1);
			popupContent.getElements().add(buildButton);
		}
		
		PopupRenderer.create(Popup.builder()
				.title(shipData.model.toUpperCase())
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.build());
	}

	@Override
	public void dispose() {
		if (shipButtons == null) return;
		for (Button shipButton : shipButtons) {
			shipButton.dispose();
		}
	}

}
