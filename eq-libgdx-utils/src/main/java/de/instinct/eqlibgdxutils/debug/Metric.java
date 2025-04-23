package de.instinct.eqlibgdxutils.debug;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class Metric<T extends Object> {

	private String tag;
	private T value;

	public abstract String getValueString();
}
