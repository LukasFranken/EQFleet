package de.instinct.eqfleet.menu.common.synchronizer.model;

public interface Retainer<T extends Object, E extends Object> {
	
	public void update(T baseElement, E synchronizedElement);

}
