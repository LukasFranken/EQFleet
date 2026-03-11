package de.instinct.eqfleet.menu.common.architecture;

import de.instinct.api.core.modules.MenuModule;

public abstract class BaseModule {
	
	public abstract MenuModule getMenuModule();
	
	public abstract void init();
	
	public abstract void load();

	public abstract void update();
	
	public abstract void close();
	
}
