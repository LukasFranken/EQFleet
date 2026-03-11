package de.instinct.eqfleet.menu.common.synchronizer.model;

public interface Disposer<E extends Object> {

	void execute(E element);
	
}
