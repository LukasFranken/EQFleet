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
public class FloatMetric extends Metric<Float> {

	@Builder.Default
	private int decimals = 2;

	@Override
	public String getValueString() {
		return StringUtils.formatFixed(getValue() == null ? 0F : getValue(), decimals);
	}

}