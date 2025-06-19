package de.instinct.eqfleet.menu.module.ship;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.shipyard.dto.ShipBlueprint;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.ship.message.UnuseShipMessage;
import de.instinct.eqfleet.menu.module.ship.message.UseShipMessage;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
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
		Label hangarLabel = new Label("Hangar: " + ShipyardModel.shipyard.getOwnedShips().size() + "/" + ShipyardModel.shipyard.getSlots());
		hangarLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		hangarLabel.setBounds(labelBounds);
		hangarLabel.render();
		
		int active = 0;
		for (ShipBlueprint ship : ShipyardModel.shipyard.getOwnedShips()) {
			if (ship.isInUse()) {
				active++;
			}
		}
		Label activeLabel = new Label("Active: " + active + "/" + ShipyardModel.shipyard.getActiveShipSlots());
		activeLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
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
				shipButtons.add(createShipButton(ship));
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
		ElementList popupContent = new ElementList();
		popupContent.setMargin(10f);
		popupContent.getElements().add(createLabelStack("TYPE", ship.getType().toString()));
		popupContent.getElements().add(createLabelStack("COST", ship.getCost() + ""));
		popupContent.getElements().add(createLabelStack("CP COST", ship.getCommandPointsCost() + ""));
		popupContent.getElements().add(createLabelStack("SPEED", StringUtils.format(ship.getMovementSpeed(), 0) + ""));
		popupContent.getElements().add(createLabelStack("--------", "--------"));
		popupContent.getElements().add(createLabelStack("WEAPON", ship.getWeapon().getType().toString()));
		popupContent.getElements().add(createLabelStack("DAMAGE", StringUtils.format(ship.getWeapon().getDamage(), 0)));
		popupContent.getElements().add(createLabelStack("RANGE", StringUtils.format(ship.getWeapon().getRange(), 0)));
		popupContent.getElements().add(createLabelStack("COOLDOWN", StringUtils.format(ship.getWeapon().getCooldown()/1000f, 1) + "s"));
		popupContent.getElements().add(createLabelStack("SPEED", StringUtils.format(ship.getWeapon().getSpeed(), 0)));
		popupContent.getElements().add(createLabelStack("--------", "--------"));
		popupContent.getElements().add(createLabelStack("DEFENSE", ""));
		popupContent.getElements().add(createLabelStack("ARMOR", StringUtils.format(ship.getDefense().getArmor(), 0)));
		popupContent.getElements().add(createLabelStack("SHIELD", StringUtils.format(ship.getDefense().getShield(), 0)));
		popupContent.getElements().add(createLabelStack("SHIELD/SEC", StringUtils.format(ship.getDefense().getShieldRegenerationSpeed(), 1)));
		
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
		useButton.setFixedWidth(200);
		popupContent.getElements().add(useButton);
		
		PopupRenderer.create(Popup.builder()
				.title(ship.getModel())
				.contentContainer(popupContent)
				.build());
	}
	
	private ElementStack createLabelStack(String label, String value) {
		ElementStack uuidLabelStack = new ElementStack();
		Label uuidLabel = new Label(label);
		uuidLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		uuidLabel.setFixedWidth(popupWidth);
		uuidLabelStack.getElements().add(uuidLabel);
		
		Label uuidValueLabel = new Label(value);
		uuidValueLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		uuidValueLabel.setFixedWidth(popupWidth);
		uuidValueLabel.setType(FontType.SMALL);
		uuidLabelStack.getElements().add(uuidValueLabel);
		return uuidLabelStack;
	}

	@Override
	public void dispose() {
		if (shipButtons == null) return;
		for (ColorButton shipButton : shipButtons) {
			shipButton.dispose();
		}
	}

}
