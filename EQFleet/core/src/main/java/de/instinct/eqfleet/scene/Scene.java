package de.instinct.eqfleet.scene;

public abstract class Scene {
	
	public abstract void init();
	
	public abstract void open();
	
	public abstract void close();
	
	public abstract void update();
	
	public abstract void render();
	
	public abstract void dispose();

}
