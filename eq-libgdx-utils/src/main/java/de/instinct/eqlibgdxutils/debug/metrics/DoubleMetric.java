package de.instinct.eqlibgdxutils.debug.metrics;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.Metric;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DoubleMetric extends Metric<Double> {

	@Builder.Default
	private int decimals = 2;

	@Override
	public String getValueString() {
		return StringUtils.format(getValue() == null ? 0D : getValue(), decimals);
	}

}