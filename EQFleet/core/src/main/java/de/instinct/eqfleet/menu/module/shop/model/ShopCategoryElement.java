package de.instinct.eqfleet.menu.module.shop.model;

import java.util.List;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopCategoryElement {
	
	private Label nameLabel;
	private List<ShopItemElement> itemElements;
	
	public void dispose() {
		if (nameLabel != null) nameLabel.dispose();
		if (itemElements != null) {
			for (ShopItemElement item : itemElements) {
				item.dispose();
			}
		}
	}

}
