package de.instinct.eqfleet.menu.module.ship;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.ModuleMessage;
import de.instinct.eqfleet.net.WebManager;

public class Shipyard extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.SHIPYARD;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void open() {
		loadData();
	}
	
	private void loadData() {
		WebManager.enqueue(
				() -> API.shipyard().data(API.authKey),
			    result -> {
			    	ShipyardModel.shipyard = result;
			    	super.requireUIReload();
			    }
		);
	}

	@Override
	public void update() {
		
	}

	@Override
	public boolean process(ModuleMessage message) {
		return false;
	}

}
