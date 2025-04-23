package de.instinct.eqlibgdxutils.debug.metrics;

import com.badlogic.gdx.math.Vector3;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.Metric;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class VectorMetric extends Metric<Vector3> {

	@Builder.Default
	private int decimals = 3;

	@Override
	public String getValueString() {
		return getValue() == null ? "null" : getVectorString();
	}

	private String getVectorString() {
		return "X" + StringUtils.format(getValue().x, decimals) + " Y" + StringUtils.format(getValue().y, decimals) + (getValue().z == 0 ? "" : " Z" + StringUtils.format(getValue().z, decimals));
	}

}