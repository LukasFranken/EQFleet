package de.instinct.eqfleet.menu.module.construction;

import de.instinct.eqfleet.menu.common.architecture.BaseModule;

public class Construction extends BaseModule {

	@Override
	public void init() {
		
	}

	@Override
	public void load() {
		loadData();
	}
	
	private void loadData() {
		//Menu.queue(ReloadConstructionMessage.builder().build());
	}

	@Override
	public void update() {
		
	}

	/*public boolean process(ModuleMessage message) {
		if (message instanceof ReloadConstructionMessage) {
			WebManager.enqueue(
					() -> API.construction().data(API.authKey),
				    result -> {
				    	ConstructionModel.playerInfrastructure = result;
				    }
			);
			WebManager.enqueue(
					() -> API.construction().construction(),
				    result -> {
				    	ConstructionModel.infrastructure = result;
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
	}*/

	@Override
	public void close() {
		
	}

}
