package de.instinct.eqfleet.menu.module.shop;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.shop.dto.Purchase;
import de.instinct.api.shop.dto.ShopCategory;
import de.instinct.api.shop.dto.ShopItem;
import de.instinct.api.shop.dto.ShopItemStage;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.inventory.InventoryModel;
import de.instinct.eqfleet.menu.module.shop.message.BuyMessage;
import de.instinct.eqfleet.menu.module.shop.model.ShopCategoryElement;
import de.instinct.eqfleet.menu.module.shop.model.ShopItemElement;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class ShopRenderer extends BaseModuleRenderer {
	
	private List<ShopCategoryElement> categoryElements;
	
	private int categoryCount;
	private int itemCount;
	
	private float categoryHeight = 40f;
	private float itemHeight = 20f;

	@Override
	public void render() {
		if (categoryElements == null || categoryElements.isEmpty()) {
			return;
		}
		renderElements();
	}

	private void renderElements() {
		float horizontalOffset = 20f;
		categoryCount = 0;
		itemCount = 0;
		for (ShopCategoryElement category : categoryElements) {
			categoryCount++;
			category.getNameLabel().setPosition(
					MenuModel.moduleBounds.x + 10f, 
					getElementYPos());
			category.getNameLabel().render();
			for (ShopItemElement item : category.getItemElements()) {
				itemCount++;
				float elementYPos = getElementYPos();
				item.getNameLabel().setPosition(
						MenuModel.moduleBounds.x + horizontalOffset, 
						elementYPos);
				item.getNameLabel().render();
				
				item.getBuyButton().setPosition(
						MenuModel.moduleBounds.x + MenuModel.moduleBounds.width - horizontalOffset - item.getBuyButton().getFixedWidth(), 
						elementYPos);
				item.getBuyButton().render();
				
				item.getCreditsIcon().setPosition(
						item.getBuyButton().getBounds().x - 18f,
						elementYPos + 2);
				item.getCreditsIcon().render();
				
				item.getPriceLabel().setPosition(
						item.getBuyButton().getBounds().x - item.getPriceLabel().getFixedWidth() - 20f, 
						elementYPos);
				item.getPriceLabel().render();
				
				item.getDescriptionLabel().setPosition(
						MenuModel.moduleBounds.x + horizontalOffset + item.getNameLabel().getFixedWidth(), 
						elementYPos);
				item.getDescriptionLabel().setFixedWidth(item.getPriceLabel().getBounds().x - item.getDescriptionLabel().getBounds().x);
				item.getDescriptionLabel().render();
			}
		}
	}

	private float getElementYPos() {
		float itemOffset = 10f;
		return MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - ((categoryHeight + itemOffset) * categoryCount) - ((itemHeight + itemOffset) * itemCount);
	}

	@Override
	public void reload() {
		dispose();
		categoryElements = new ArrayList<>();
		if (ShopModel.shopData != null && ShopModel.shopData.getCategories() != null) {
			for (ShopCategory category : ShopModel.shopData.getCategories()) {
				categoryElements.add(createCategoryElement(category));
			}
		}
	}

	private ShopCategoryElement createCategoryElement(ShopCategory category) {
		Label nameLabel = new Label(category.getName());
		nameLabel.setType(FontType.LARGE);
		nameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nameLabel.setFixedHeight(categoryHeight);
		nameLabel.setFixedWidth(MenuModel.moduleBounds.getWidth() - 20f);
		
		List<ShopItemElement> itemElements = new ArrayList<>();
		if (category.getItems() != null) {
			for (ShopItem item : category.getItems()) {
				itemElements.add(createItemElement(item));
			}
		}
		
		return ShopCategoryElement.builder()
				.nameLabel(nameLabel)
				.itemElements(itemElements)
				.build();
	}

	private ShopItemElement createItemElement(ShopItem item) {
		ShopItemStage currentStage = getCurrentStage(item);
		return ShopItemElement.builder()
				.item(item)
				.nameLabel(createItemNameLabel(item.getName()))
				.descriptionLabel(createDescriptionLabel(currentStage.getDescription()))
				.priceLabel(createPriceLabel(currentStage.getPrice()))
				.creditsIcon(createCreditsIcon(currentStage.getPrice()))
				.buyButton(createBuyButton(item.getId()))
				.build();
	}

	private ShopItemStage getCurrentStage(ShopItem item) {
		int currentStageId = 0;
		for (Purchase purchase : ShopModel.shopData.getPurchaseHistory()) {
			if (purchase.getItemId() == item.getId()) {
				currentStageId++;
			}
		}
		return item.getStages().get(currentStageId);
	}

	private Label createItemNameLabel(String name) {
		Label nameLabel = new Label(name);
		nameLabel.setType(FontType.NORMAL);
		nameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nameLabel.setFixedWidth(FontUtil.getFontTextWidthPx(12, nameLabel.getType()));
		nameLabel.setFixedHeight(itemHeight);
		return nameLabel;
	}
	
	private Label createDescriptionLabel(String description) {
		Label descriptionLabel = new Label(description);
		descriptionLabel.setType(FontType.SMALL);
		descriptionLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		descriptionLabel.setFixedHeight(itemHeight);
		return descriptionLabel;
	}
	
	private Label createPriceLabel(long price) {
		Label priceLabel = new Label(StringUtils.formatBigNumber(price));
		priceLabel.setType(FontType.SMALL);
		priceLabel.setColor(price > InventoryModel.resources.getCredits() ? Color.RED : Color.GREEN);
		priceLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		priceLabel.setFixedWidth(FontUtil.getFontTextWidthPx(8, priceLabel.getType()));
		priceLabel.setFixedHeight(itemHeight);
		return priceLabel;
	}
	
	private Image createCreditsIcon(long price) {
		Image creditsIcon = new Image(TextureManager.getTexture("ui/image", price > InventoryModel.resources.getCredits() ? "credits_red" : "credits"));
		creditsIcon.setFixedWidth(16f);
		creditsIcon.setFixedHeight(16f);
		return creditsIcon;
	}

	private ColorButton createBuyButton(int id) {
		ColorButton buyButton = DefaultButtonFactory.colorButton("Buy", new Action() {
			
			@Override
			public void execute() {
				Menu.queue(BuyMessage.builder()
						.itemId(id)
						.build());
			}
			
		});
		buyButton.setFixedWidth(40f);
		buyButton.setFixedHeight(20f);
		return buyButton;
	}

	@Override
	public void dispose() {
		if (categoryElements != null) {
			for (ShopCategoryElement category : categoryElements) {
				category.dispose();
			}
		}
	}

}
