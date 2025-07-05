package de.instinct.eqfleet.menu.module.shop.model;

import de.instinct.api.shop.dto.ShopItem;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopItemElement {
	
	private ShopItem item;
	private Label nameLabel;
	private Label descriptionLabel;
	private Label priceLabel;
	private Image creditsIcon;
	private ColorButton buyButton;
	
	public void dispose() {
		if (nameLabel != null) nameLabel.dispose();
		if (descriptionLabel != null) descriptionLabel.dispose();
		if (priceLabel != null) priceLabel.dispose();
		if (buyButton != null) buyButton.dispose();
	}

}
