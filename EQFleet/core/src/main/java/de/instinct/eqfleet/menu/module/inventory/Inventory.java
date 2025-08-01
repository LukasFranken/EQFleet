package de.instinct.eqfleet.menu.module.inventory;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.meta.dto.Resource;
import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import de.instinct.eqfleet.menu.module.inventory.message.LoadResourcesMessage;
import de.instinct.eqfleet.net.WebManager;

public class Inventory extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.INVENTORY;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open() {
		loadData();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof LoadResourcesMessage) {
			loadData();
			return true;
		}
		return false;
	}
	
	private void loadData() {
		WebManager.enqueue(
			    () -> API.meta().resources(API.authKey),
			    result -> {
			        InventoryModel.resources = result;
			    }
		);
	}

	@Override
	public void close() {
		
	}
	
	public static long getResource(Resource type) {
		for (ResourceAmount amount : InventoryModel.resources.getResources()) {
			if (amount.getType() == type) {
				return amount.getAmount();
			}
		}
		return 0;
	}

}
