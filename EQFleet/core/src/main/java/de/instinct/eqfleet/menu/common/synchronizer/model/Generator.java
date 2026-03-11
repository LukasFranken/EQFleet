package de.instinct.eqfleet.menu.common.synchronizer.model;

public interface Generator<T extends Object, E extends Object> {
	
	E generate(T baseElement);

}
