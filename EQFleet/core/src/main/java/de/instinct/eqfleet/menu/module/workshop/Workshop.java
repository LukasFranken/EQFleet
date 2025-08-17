package de.instinct.eqfleet.menu.module.workshop;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.shipyard.dto.ShipBuildResponse;
import de.instinct.api.shipyard.dto.ShipUpgradeResponse;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import de.instinct.eqfleet.menu.module.profile.inventory.message.LoadResourcesMessage;
import de.instinct.eqfleet.menu.module.ship.ShipyardModel;
import de.instinct.eqfleet.menu.module.workshop.message.BuildShipMessage;
import de.instinct.eqfleet.menu.module.workshop.message.ReloadWorkshopMessage;
import de.instinct.eqfleet.menu.module.workshop.message.UpgradeShipMessage;
import de.instinct.eqfleet.net.WebManager;

public class Workshop extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.WORKSHOP;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void open() {
		loadData();
	}

	private void loadData() {
		Menu.queue(ReloadWorkshopMessage.builder().build());
	}

	@Override
	public void update() {
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof ReloadWorkshopMessage) {
			WebManager.enqueue(
					() -> API.shipyard().data(API.authKey),
				    result -> {
				    	ShipyardModel.playerShipyard = result;
				    	super.requireUIReload();
				    }
			);
			WebManager.enqueue(
					() -> API.shipyard().shipyard(),
				    result -> {
				    	ShipyardModel.shipyard = result;
				    	super.requireUIReload();
				    }
			);
			return true;
		}
		if (message instanceof BuildShipMessage) {
			BuildShipMessage buildShipMessage = (BuildShipMessage) message;
			WebManager.enqueue(
					() -> API.shipyard().build(buildShipMessage.getShipUUID()),
				    result -> {
				    	WorkshopModel.shipBuildResponse = result;
				    	if (result == ShipBuildResponse.SUCCESS) {
				    		Menu.queue(LoadResourcesMessage.builder().build());
				    		loadData();
				    	} else {
				    		super.requireUIReload();
				    	}
				    }
			);
			return true;
		}
		if (message instanceof UpgradeShipMessage) {
			UpgradeShipMessage buildShipMessage = (UpgradeShipMessage) message;
			WebManager.enqueue(
					() -> API.shipyard().upgrade(buildShipMessage.getShipUUID()),
				    result -> {
				    	WorkshopModel.shipUpgradeResponse = result;
				    	if (result == ShipUpgradeResponse.SUCCESS) {
				    		Menu.queue(LoadResourcesMessage.builder().build());
				    		loadData();
				    	} else {
				    		super.requireUIReload();
				    	}
				    }
			);
			return true;
		}
		return false;
	}

}
