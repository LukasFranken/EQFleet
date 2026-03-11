package de.instinct.eqfleet.menu.module.shop;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.meta.dto.Resource;
import de.instinct.api.shop.dto.Purchase;
import de.instinct.api.shop.dto.ShopCategory;
import de.instinct.api.shop.dto.item.ShopItem;
import de.instinct.api.shop.dto.item.ShopItemStage;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.synchronizer.ListSynchronizer;
import de.instinct.eqfleet.menu.common.synchronizer.SynchronizationConfiguration;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.profile.ProfileModuleAPI;
import de.instinct.eqfleet.menu.module.shop.message.types.BuyMessage;
import de.instinct.eqfleet.menu.module.shop.model.ShopCategoryElement;
import de.instinct.eqfleet.menu.module.shop.model.ShopItemElement;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class ShopRenderer extends BaseModuleRenderer {

	private ListSynchronizer<ShopCategory, ShopCategoryElement> categorySynchronizer;
	private ListSynchronizer<ShopItem, ShopItemElement> itemSynchronizer;
	
	private List<ShopCategoryElement> categoryElements;

	private int categoryCount;
	private int itemCount;

	private float categoryHeight = 40f;
	private float itemHeight = 20f;
	
	@Override
	public void init() {
		categoryElements = new ArrayList<>();
		
		itemSynchronizer = ListSynchronizer.<ShopItem, ShopItemElement>builder()
				.synchronizationConfiguration(SynchronizationConfiguration.<ShopItem, ShopItemElement>builder()
						.comparator((item, element) -> item.getId() == element.getItem().getId())
						.generator(this::createItemElement)
						.disposer(ShopItemElement::dispose)
						.build())
				.build();
		
		categorySynchronizer = ListSynchronizer.<ShopCategory, ShopCategoryElement>builder()
				.synchronizationConfiguration(SynchronizationConfiguration.<ShopCategory, ShopCategoryElement>builder()
						.comparator((category, element) -> category.getName().equals(element.getNameLabel().getText()))
						.retainer((category, element) -> {
							itemSynchronizer.update(category.getItems(), element.getItemElements());
						})
						.generator(this::createCategoryElement)
						.disposer(ShopCategoryElement::dispose)
						.build())
				.build();
	}
	
	private ShopCategoryElement createCategoryElement(ShopCategory category) {
		Label nameLabel = new Label(category.getName());
		nameLabel.setType(FontType.LARGE);
		nameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nameLabel.setFixedHeight(categoryHeight);
		nameLabel.setFixedWidth(MenuModel.moduleBounds.getWidth() - 20f);
		List<ShopItemElement> itemElements = new ArrayList<>();
		itemSynchronizer.update(category.getItems(), itemElements);
		return ShopCategoryElement.builder()
				.nameLabel(nameLabel)
				.itemElements(itemElements)
				.build();
	}

	private ShopItemElement createItemElement(ShopItem item) {
		return ShopItemElement.builder()
				.item(item)
				.nameLabel(createItemNameLabel(item.getName()))
				.descriptionLabel(createDescriptionLabel())
				.priceLabel(createPriceLabel())
				.creditsIcon(createCreditsIcon())
				.buyButton(createBuyButton(item.getId()))
				.active(true)
				.build();
	}

	private Label createItemNameLabel(String name) {
		Label nameLabel = new Label(name);
		nameLabel.setType(FontType.NORMAL);
		nameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nameLabel.setFixedWidth(FontUtil.getFontTextWidthPx(12, nameLabel.getType()));
		nameLabel.setFixedHeight(itemHeight);
		return nameLabel;
	}

	private Label createDescriptionLabel() {
		Label descriptionLabel = new Label("");
		descriptionLabel.setType(FontType.SMALL);
		descriptionLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		descriptionLabel.setFixedHeight(itemHeight);
		return descriptionLabel;
	}

	private Label createPriceLabel() {
		Label priceLabel = new Label("");
		priceLabel.setType(FontType.SMALL);
		priceLabel.setColor(Color.GREEN);
		priceLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		priceLabel.setFixedWidth(FontUtil.getFontTextWidthPx(8, priceLabel.getType()));
		priceLabel.setFixedHeight(itemHeight);
		return priceLabel;
	}

	private Image createCreditsIcon() {
		Image creditsIcon = new Image(TextureManager.getTexture("ui/image", "credits"));
		creditsIcon.setFixedWidth(16f);
		creditsIcon.setFixedHeight(16f);
		return creditsIcon;
	}

	private ColorButton createBuyButton(int id) {
		ColorButton buyButton = DefaultButtonFactory.colorButton("Buy", new Action() {

			@Override
			public void execute() {
				ShopModel.messageQueue.add(BuyMessage.builder().itemId(id).build());
			}

		});
		buyButton.setFixedWidth(40f);
		buyButton.setFixedHeight(20f);
		return buyButton;
	}

	@Override
	public void update() {
		if (ShopModel.shopData == null || ShopModel.playerShopData == null) return;
		if (ShopModel.dataUpdated) {
			categorySynchronizer.update(ShopModel.shopData.getCategories(), categoryElements);
			ShopModel.dataUpdated = false;
		}
		
		float horizontalOffset = 20f;
		categoryCount = 0;
		itemCount = 0;
		for (ShopCategoryElement category : categoryElements) {
			categoryCount++;
			category.getNameLabel().setPosition(MenuModel.moduleBounds.x + 10f, getElementYPos());
			for (ShopItemElement item : category.getItemElements()) {
				ShopItemStage currentStage = getCurrentStage(item.getItem());
				if (currentStage == null) {
					item.setActive(false);
					continue;
				} else {
					item.setActive(true);
					item.getDescriptionLabel().setText(currentStage.getDescription());
					item.getPriceLabel().setText(StringUtils.formatBigNumber(currentStage.getPrice()));
					if (currentStage.getPrice() > ProfileModuleAPI.getResource(Resource.CREDITS)) {
						item.getPriceLabel().setColor(Color.RED);
						item.getCreditsIcon().setTexture(TextureManager.getTexture("ui/image", "credits_red"));
					} else {
						item.getPriceLabel().setColor(Color.GREEN);
						item.getCreditsIcon().setTexture(TextureManager.getTexture("ui/image", "credits"));
					}
				}
				itemCount++;
				float elementYPos = getElementYPos();
				item.getNameLabel().setPosition(MenuModel.moduleBounds.x + horizontalOffset, elementYPos);
				item.getBuyButton().setPosition(MenuModel.moduleBounds.x + MenuModel.moduleBounds.width - horizontalOffset - item.getBuyButton().getFixedWidth(), elementYPos);
				item.getCreditsIcon().setPosition(item.getBuyButton().getBounds().x - 18f, elementYPos + 2);
				item.getPriceLabel().setPosition(item.getBuyButton().getBounds().x - item.getPriceLabel().getFixedWidth() - 20f, elementYPos);
				item.getDescriptionLabel().setPosition(MenuModel.moduleBounds.x + horizontalOffset + item.getNameLabel().getFixedWidth(), elementYPos);
				item.getDescriptionLabel().setFixedWidth(item.getPriceLabel().getBounds().x - item.getDescriptionLabel().getBounds().x);
			}
		}
	}
	
	private ShopItemStage getCurrentStage(ShopItem item) {
		int currentStageId = 0;
		for (Purchase purchase : ShopModel.playerShopData.getPurchaseHistory()) {
			if (purchase.getItemId() == item.getId()) {
				currentStageId++;
			}
		}
		if (currentStageId >= item.getStages().size())
			return null;
		return item.getStages().get(currentStageId);
	}
	
	private float getElementYPos() {
		float itemOffset = 10f;
		return MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - ((categoryHeight + itemOffset) * categoryCount) - ((itemHeight + itemOffset) * itemCount);
	}

	@Override
	public void render() {
		if (categoryElements == null || categoryElements.isEmpty()) {
			return;
		}
		renderElements();
		if (ShopModel.buyResponse != null) {
			createBuyResponsePopup();
			ShopModel.buyResponse = null;
		}
	}

	private void createBuyResponsePopup() {
		PopupRenderer.createMessageDialog("Purchase Failed", ShopModel.buyResponse.getMessage() == null ? ShopModel.buyResponse.getCode().toString() : ShopModel.buyResponse.getMessage());
	}

	private void renderElements() {
		for (ShopCategoryElement category : categoryElements) {
			category.getNameLabel().render();
			for (ShopItemElement item : category.getItemElements()) {
				if (!item.isActive()) continue;
				item.getNameLabel().render();
				item.getBuyButton().render();
				item.getCreditsIcon().render();
				item.getPriceLabel().render();
				item.getDescriptionLabel().render();
			}
		}
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
