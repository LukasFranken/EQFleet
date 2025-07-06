package de.instinct.eqfleet.menu.module.starmap;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.ModuleMessage;
import de.instinct.eqfleet.menu.module.starmap.message.ReloadStarmapMessage;
import de.instinct.eqfleet.net.WebManager;

public class Starmap extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.STARMAP;
	}

	@Override
	public void init() {
		StarmapModel.selectedGalaxyId = -1;
		StarmapModel.selectedStarsystemId = -1;
	}

	@Override
	public void open() {
		Menu.queue(ReloadStarmapMessage.builder().build());
	}

	@Override
	public void update() {

	}

	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof ReloadStarmapMessage) {
			WebManager.enqueue(
					() -> API.starmap().data(API.authKey),
				    result -> {
				    	StarmapModel.sector = result;
				    	super.requireUIReload();
				    }
			);
			return true;
		}
		return false;
	}

}
