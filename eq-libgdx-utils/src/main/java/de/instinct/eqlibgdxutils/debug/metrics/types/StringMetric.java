package de.instinct.eqlibgdxutils.debug.metrics.types;

import de.instinct.eqlibgdxutils.debug.metrics.Metric;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class StringMetric extends Metric<String> {

	@Override
	public String getValueString() {
		return getValue() == null ? "" : getValue();
	}

}