package de.instinct.eqfleet.menu.common.architecture;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.ModuleMessage;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public abstract class BaseModule {
	
	protected void requireUIReload() {
		Menu.requireReload(getMenuModule());
	}
	
	public abstract MenuModule getMenuModule();
	
	public abstract void init();
	
	public abstract void open();

	public abstract void update();
	
	public abstract void close();
	
	public void processBackendMessage(ModuleMessage message) {
		if (!process(message)) {
			Logger.log("BaseModule", "Unhandles ModuleMessage: " + message);
		}
	}

	public abstract boolean process(ModuleMessage message);
	
}
