package de.instinct.eqfleet.menu.module.ship;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.meta.dto.ShipData;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;

public class ShipyardRenderer extends BaseModuleRenderer {

	private List<ColorButton> shipButtons;
	
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
		ColorButton shipButton = DefaultButtonFactory.colorButton(ship.getModel(), new Action() {
			
			@Override
			public void execute() {
				ShipyardModel.selectedShipUUID = ship.getUuid();
				
				ElementList popupContent = new ElementList();
				popupContent.setMargin(10f);
				Label testLabel = new Label(ship.getModel());
				popupContent.getElements().add(testLabel);
				Label testLabel2 = new Label(ship.getModel());
				popupContent.getElements().add(testLabel2);
				PopupRenderer.create(Popup.builder()
						.title(ship.getModel())
						.contentContainer(popupContent)
						.build());
			}
			
		});
		return shipButton;
	}

	@Override
	public void dispose() {
		for (ColorButton shipButton : shipButtons) {
			shipButton.dispose();
		}
	}

}
