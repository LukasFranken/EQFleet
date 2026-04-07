package de.instinct.eqlibgdxutils.debug.modulator.modulation.types;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.modulator.modulation.Modulation;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.LabelUpdateAction;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.LabeledSlider;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.ValueChangeAction;

public class RangeModulation extends Modulation {
	
	private LabeledSlider rangeSlider;
	
	public RangeModulation(String tag, ValueChangeAction valueChangeAction, float initialValue) {
		super(tag);
		createSlider(valueChangeAction, initialValue, new LabelUpdateAction() {
			
			@Override
			public String getLabelText(float value) {
				return StringUtils.format(value, 2);
			}
			
		});
	}
	
	public RangeModulation(String tag, ValueChangeAction valueChangeAction, float initialValue, LabelUpdateAction labelUpdateAction) {
		super(tag);
		createSlider(valueChangeAction, initialValue, labelUpdateAction);
	}

	private void createSlider(ValueChangeAction valueChangeAction, float initialValue, LabelUpdateAction labelUpdateAction) {
		rangeSlider = new LabeledSlider(valueChangeAction, initialValue, labelUpdateAction);
		rangeSlider.getSlider().getSliderButton().setConsoleBypass(true);
	}

	@Override
	public void renderModulation(Rectangle bounds) {
		rangeSlider.setBounds(bounds);
		rangeSlider.render();
	}

}
