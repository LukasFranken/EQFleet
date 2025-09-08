package de.instinct.eqlibgdxutils.rendering.ui.component.active.slider;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LabeledSlider extends Component {
	
	private Slider slider;
	private Label label;
	private int labelChars;
	private LabelUpdateAction labelUpdateAction;
	private float labelSliderMargin;
	private float labelInnerMargin;
	
	public LabeledSlider(ValueChangeAction valueChangeAction, float initialValue, LabelUpdateAction labelUpdateAction) {
		this.labelUpdateAction = labelUpdateAction;
		slider = new Slider(valueChangeAction, initialValue);
		labelChars = 3;
		label = new Label("0");
		label.setType(FontType.SMALL);
		Border labelBorder = new Border();
		labelBorder.setColor(new Color(SkinManager.skinColor));
		labelBorder.setSize(2f);
		label.setBorder(labelBorder);
		labelSliderMargin = 10f;
		labelInnerMargin = 10f;
	}
	
	@Override
	public float calculateWidth() {
		return getBounds().width;
	}
	
	@Override
	public float calculateHeight() {
		return getBounds().height;
	}
	
	@Override
	protected void updateComponent() {
		label.setFixedHeight(getBounds().height);
		label.setFixedWidth(FontUtil.getFontTextWidthPx(labelChars, label.getType()) + labelInnerMargin);
		label.setText(labelUpdateAction.getLabelText(slider.getCurrentValue()));
		label.getBorder().setBounds(getBounds());
		label.setAlpha(getAlpha());
		
		Rectangle sliderBounds = new Rectangle(getBounds());
		sliderBounds.width -= label.getBounds().width + labelSliderMargin;
		slider.setBounds(sliderBounds);
		slider.setAlpha(getAlpha());
	}
	
	@Override
	protected void renderComponent() {
		label.setPosition(getBounds().x + getBounds().width - label.getFixedWidth(), getBounds().y);
		label.render();
		slider.render();
	}

	@Override
	public void dispose() {
		slider.dispose();
		label.dispose();
	}

}
