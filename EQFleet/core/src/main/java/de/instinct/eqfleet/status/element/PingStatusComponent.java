package de.instinct.eqfleet.status.element;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.metrics.MetricUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScale;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScaleLoader;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class PingStatusComponent extends Component {
	
	private ColorScaleLoader colorScaleLoader;
	private Label pingLabel;
	
	public PingStatusComponent() {
		super();
		colorScaleLoader = new ColorScaleLoader();
		pingLabel = new Label("");
		pingLabel.setColor(new Color(SkinManager.skinColor));
		pingLabel.setType(FontType.TINY);
		pingLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
	}
	
	@Override
	protected void updateComponent() {
		pingLabel.setBounds(getBounds());
		String pingMetricValue = MetricUtil.getValue(WebManager.PING_METRIC_TAG);
		float pingMS = pingMetricValue.contentEquals("null") ? 0 : Float.parseFloat(pingMetricValue);
		String pingText = pingMS >= 1000 ? ">999" : StringUtils.format(pingMS, 0);
		pingLabel.setText(pingText + "MS");
		pingLabel.setColor(colorScaleLoader.load(ColorScale.GREEN_TO_RED, 1 - MathUtil.clamp(pingMS, 0f, 300f) / 300f));
	}

	@Override
	protected void renderComponent() {
		pingLabel.render();
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		pingLabel.dispose();
	}

}
