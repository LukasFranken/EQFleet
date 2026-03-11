package de.instinct.eqfleet.menu.common.synchronizer.model;

public interface Comparator<T extends Object, E extends Object> {
	
	boolean equals(T element1, E element2);

}
