package de.instinct.eqlibgdxutils.debug.metrics;

import de.instinct.eqlibgdxutils.debug.Metric;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class StringMetric extends Metric<String> {

	@Override
	public String getValueString() {
		return getValue() == null ? "" : getValue();
	}

}