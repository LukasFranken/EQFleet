package de.instinct.engine.core.order;

import lombok.ToString;

@ToString
public abstract class GameOrder {

	public long processGameTimeStamp;
	
	public GameOrder clone() {
		try {
			return (GameOrder) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cloning not supported for GameOrder", e);
		}
	}

}
