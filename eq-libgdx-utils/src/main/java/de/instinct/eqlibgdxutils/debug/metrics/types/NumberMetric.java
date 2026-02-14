package de.instinct.eqlibgdxutils.debug.metrics.types;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.metrics.Metric;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NumberMetric extends Metric<Number> {
	
	@Builder.Default
	private int decimals = 2;

	@Override
	public String getValueString() {
		if (getValue() == null) {
			return "null";
		} else {
			if (getValue().longValue() > 1000) {
				long value = getValue().longValue();
				return StringUtils.formatBigNumber(value);
			} else {
				double value = getValue().doubleValue();
				return StringUtils.formatFixed(value, decimals);
			}
		}
	}
	
}
