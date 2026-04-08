package de.instinct.eqfleet.menu.common.architecture;

public abstract class BaseModule {
	
	public abstract void init();
	
	public abstract void load();

	public abstract void update();
	
	public abstract void close();
	
}
