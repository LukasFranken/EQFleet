package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.CycleCompletionHook;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CyclicalPlainRectangularLoadingBar extends PlainRectangularLoadingBar {
	
	private double rate;
	private boolean projection;
	private boolean cyclical;
	private CycleCompletionHook cycleCompletionHook;
	private float maxCyclicalRate;
	
	public CyclicalPlainRectangularLoadingBar() {
		super();
		maxCyclicalRate = 10f;
		cyclical = true;
		projection = true;
	}
	
	@Override
	protected void renderLabel() {
		if (isOverRateThreshold()) {
			setCustomDescriptor(StringUtils.format(getRate() / getMaxValue(), 2) + "/s");
		}
		super.renderLabel();
	}
	
	private boolean isOverRateThreshold() {
		return isCyclical() && getRate() > getMaxValue() * maxCyclicalRate && isProjection();
	}

}
