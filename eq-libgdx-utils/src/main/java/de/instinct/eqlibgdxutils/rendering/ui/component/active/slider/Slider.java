package de.instinct.eqlibgdxutils.rendering.ui.component.active.slider;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Slider extends Component {
	
	private ColorButton sliderButton;
	
	private int labelCharCount;
	private ValueChangeAction valueChangeAction;
	private Action dragEndAction;
	
	private float initialMouseDelta;
	private float currentValue;
	
	private EQRectangle sliderLineShape;
	
	public Slider(ValueChangeAction valueChangeAction, float initialValue) {
		super();
		this.valueChangeAction = valueChangeAction;
		this.currentValue = initialValue;
		sliderButton = new ColorButton("");
		sliderButton.setColor(Color.BLACK);
		sliderButton.setLabelColor(new Color(SkinManager.skinColor));
		sliderButton.setHoverColor(new Color(SkinManager.darkestSkinColor));
		sliderButton.setDownColor(Color.BLACK);
		labelCharCount = 3;
		sliderLineShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color(SkinManager.darkerSkinColor))
				.filled(true)
				.build();
		initialMouseDelta = -1;
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
		updateValue();
		
		float sliderXPos = MathUtil.linear(getBounds().x, getMaxButtonXPos(), currentValue);
		sliderButton.setBounds(sliderXPos, getBounds().y, getBounds().height, getBounds().height);
		sliderButton.setAlpha(getAlpha());
		
		sliderLineShape.getBounds().set(getBounds().x, getBounds().y + (getBounds().height / 2) + 1, getBounds().width, 2);
		sliderLineShape.getColor().a = getAlpha();
	}

	private void updateValue() {
		if (initialMouseDelta == -1) {
			if (sliderButton.isDown()) {
				initialMouseDelta = InputUtil.getVirtualMousePosition().x - sliderButton.getBounds().x;
			}
		} else {
			if (InputUtil.isPressed()) {
				valueChanged();
			} else {
				dragEnded();
				initialMouseDelta = -1;
			}
		}
	}

	private void valueChanged() {
		currentValue = MathUtil.clamp(((InputUtil.getVirtualMousePosition().x - initialMouseDelta) - getBounds().x) / (getMaxButtonXPos() - getBounds().x), 0f, 1f);
		if (valueChangeAction != null) valueChangeAction.execute(currentValue);
	}
	
	private void dragEnded() {
		if (dragEndAction != null) dragEndAction.execute();
	}

	@Override
	protected void renderComponent() {
		Shapes.draw(sliderLineShape);
		sliderButton.render();
	}

	private float getMaxButtonXPos() {
		return getBounds().x + getBounds().width - sliderButton.getBounds().width;
	}

	@Override
	public void dispose() {
		sliderButton.dispose();
	}

}
