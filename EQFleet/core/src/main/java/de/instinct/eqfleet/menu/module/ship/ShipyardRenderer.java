package de.instinct.eqfleet.menu.module.ship;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.meta.dto.ShipData;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.ship.message.UseShipMessage;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;

public class ShipyardRenderer extends BaseModuleRenderer {

	private List<ColorButton> shipButtons;
	
	private final float popupWidth = 200f;
	
	@Override
	public void render() {
		if (shipButtons != null) {
			renderShips();
		}
	}

	private void renderShips() {
		int elementsPerRow = 5;
		float margin = (((float)MenuModel.moduleBounds.width) - (50 * elementsPerRow)) / ((float)(elementsPerRow + 1));
		
		int i = 0;
		for (ColorButton shipButton : shipButtons) {
			int column = i % elementsPerRow;
			int row = 1 + ((int)i / elementsPerRow);
			shipButton.setPosition(MenuModel.moduleBounds.x + margin + ((50 + margin) * column),
					MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - ((50 + margin) * row));
			shipButton.render();
			i++;
		}
	}

	@Override
	public void reload() {
		shipButtons = new ArrayList<>();
		if (ShipyardModel.shipyard != null && ShipyardModel.shipyard.getOwnedShips() != null) {
			for (ShipData ship : ShipyardModel.shipyard.getOwnedShips()) {
				shipButtons.add(createShipButton(ship));
			}
		}
	}
	
	private ColorButton createShipButton(ShipData ship) {
		ColorButton shipButton = DefaultButtonFactory.colorButton(ship.getModel().substring(0, 4), new Action() {
			
			@Override
			public void execute() {
				createShipPopup(ship);
			}
			
		});
		shipButton.getBorder().setColor(ship.isInUse() ? Color.GREEN : DefaultUIValues.skinColor);
		return shipButton;
	}
	
	private void createShipPopup(ShipData ship) {
		ElementList popupContent = new ElementList();
		popupContent.setMargin(10f);
		popupContent.getElements().add(createLabelStack("UUID", StringUtils.elide(ship.getUuid(), 4)));
		popupContent.getElements().add(createLabelStack("POWER", ship.getPower() + ""));
		popupContent.getElements().add(createLabelStack("HEALTH", ship.getHealth() + ""));
		popupContent.getElements().add(createLabelStack("COST", ship.getCost() + ""));
		popupContent.getElements().add(createLabelStack("SPEED", StringUtils.format(ship.getMovementSpeed(), 0) + ""));
		
		ColorButton useButton = DefaultButtonFactory.colorButton("Use", new Action() {
			
			@Override
			public void execute() {
				Menu.queue(UseShipMessage.builder()
						.shipUUID(ship.getUuid())
						.build());
				
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
		for (ColorButton shipButton : shipButtons) {
			shipButton.dispose();
		}
	}

}
