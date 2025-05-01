package de.instinct.eqfleet.net.model;

public interface RequestConsumer<T> {
	
	void accept(T t);

}
