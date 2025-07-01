package de.instinct.eqfleet.menu.module.shop;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.shop.dto.BuyResponse;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.ModuleMessage;
import de.instinct.eqfleet.menu.module.inventory.message.LoadResourcesMessage;
import de.instinct.eqfleet.menu.module.shop.message.BuyMessage;
import de.instinct.eqfleet.menu.module.shop.message.ReloadShopMessage;
import de.instinct.eqfleet.net.WebManager;

public class Shop extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.SHOP;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void open() {
		Menu.queue(ReloadShopMessage.builder().build());
	}

	@Override
	public void update() {
		
	}

	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof ReloadShopMessage) {
			WebManager.enqueue(
					() -> API.shop().data(API.authKey),
				    result -> {
				    	ShopModel.shopData = result;
				    	super.requireUIReload();
				    }
			);
			return true;
		}
		if (message instanceof BuyMessage) {
			WebManager.enqueue(
					() -> API.shop().buy(API.authKey, ((BuyMessage) message).getItemId()),
				    result -> {
				    	if (result == BuyResponse.SUCCESS) {
				    		Menu.queue(LoadResourcesMessage.builder().build());
				    		Menu.queue(ReloadShopMessage.builder().build());
				    	} else {
				    		
				    	}
				    }
			);
			return true;
		}
		return false;
	}

}
