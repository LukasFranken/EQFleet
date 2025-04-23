package de.instinct.eqlibgdxutils.debug.metrics;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.Metric;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class RectangleMetric extends Metric<Rectangle> {

	@Builder.Default
	private int decimals = 3;

	@Override
	public String getValueString() {
		return getValue() == null ? "null" : getRectangleString();
	}

	private String getRectangleString() {
		return "X" + StringUtils.format(getValue().x, decimals) + " Y" + StringUtils.format(getValue().y, decimals) + " W" + StringUtils.format(getValue().width, decimals) + " H" + StringUtils.format(getValue().height, decimals);
	}

}