package de.instinct.eqfleet.menu.module.shop;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.shop.dto.BuyResponseCode;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqfleet.menu.module.profile.message.types.LoadProfileMessage;
import de.instinct.eqfleet.menu.module.shop.message.ShopMessage;
import de.instinct.eqfleet.menu.module.shop.message.types.BuyMessage;
import de.instinct.eqfleet.menu.module.shop.message.types.ReloadShopMessage;
import de.instinct.eqfleet.net.WebManager;

public class Shop extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.SHOP;
	}

	@Override
	public void init() {
		ShopModel.messageQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void load() {
		loadData();
	}

	@Override
	public void update() {
		if (!ShopModel.messageQueue.isEmpty()) {
			process(ShopModel.messageQueue.poll());
		}
	}
	
	private void process(ShopMessage message) {
		if (message instanceof ReloadShopMessage) {
			loadData();
		}
		if (message instanceof BuyMessage) {
			WebManager.enqueue(
					() -> API.shop().buy(API.authKey, ((BuyMessage) message).getItemId()),
				    result -> {
				    	if (result.getCode() == BuyResponseCode.SUCCESS) {
				    		ProfileModel.messageQueue.add(LoadProfileMessage.builder().build());
				    		loadData();
				    	} else {
				    		ShopModel.buyResponse = result;
				    	}
				    }
			);
		}
	}
	
	private void loadData() {
		WebManager.enqueue(
				() -> API.shop().shop(),
			    result -> {
			    	ShopModel.shopData = result;
			    }
		);
		WebManager.enqueue(
				() -> API.shop().data(API.authKey),
			    result -> {
			    	ShopModel.playerShopData = result;
			    }
		);
		ShopModel.dataUpdated = true;
	}

	@Override
	public void close() {
		
	}

}
