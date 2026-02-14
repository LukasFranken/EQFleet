package de.instinct.eqfleet.net.model;

import lombok.Data;

@Data
public class Request<T> {
	
	private RequestAction requestAction;
	private RequestErrorConsumer errorConsumer;

}
