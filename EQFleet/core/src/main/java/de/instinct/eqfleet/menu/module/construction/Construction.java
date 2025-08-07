package de.instinct.eqfleet.menu.module.construction;

import de.instinct.api.construction.dto.UseTurretResponseCode;
import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.module.construction.message.ReloadConstructionMessage;
import de.instinct.eqfleet.menu.module.construction.message.UseTurretMessage;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import de.instinct.eqfleet.net.WebManager;

public class Construction extends BaseModule {
	
	@Override
	public MenuModule getMenuModule() {
		return MenuModule.CONSTRUCTION;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void open() {
		loadData();
	}
	
	private void loadData() {
		Menu.queue(ReloadConstructionMessage.builder().build());
	}

	@Override
	public void update() {
		
	}

	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof ReloadConstructionMessage) {
			WebManager.enqueue(
					() -> API.construction().data(API.authKey),
				    result -> {
				    	ConstructionModel.playerInfrastructure = result;
				    	super.requireUIReload();
				    }
			);
			WebManager.enqueue(
					() -> API.construction().construction(),
				    result -> {
				    	ConstructionModel.infrastructure = result;
				    	super.requireUIReload();
				    }
			);
			return true;
		}
		if (message instanceof UseTurretMessage) {
			UseTurretMessage useTurretMessage = (UseTurretMessage) message;
			WebManager.enqueue(
					() -> API.construction().use(API.authKey, useTurretMessage.getTurretUUID()),
					result -> {
						if (result == UseTurretResponseCode.SUCCESS) {
							loadData();
						}
					}
			);
			return true;
		}
		return false;
	}

	@Override
	public void close() {
		
	}

}
