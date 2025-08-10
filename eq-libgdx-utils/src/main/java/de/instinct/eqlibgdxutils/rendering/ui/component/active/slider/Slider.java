package de.instinct.eqlibgdxutils.rendering.ui.component.active.slider;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.SimpleShapeRenderer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Slider extends Component {
	
	private ColorButton sliderButton;
	
	private int labelCharCount;
	private ValueChangeAction valueChangeAction;
	private Action dragEndAction;
	
	private Vector2 lastUpdateMousePosition;
	private float currentValue;
	
	public Slider(ValueChangeAction valueChangeAction, float initialValue) {
		this.valueChangeAction = valueChangeAction;
		this.currentValue = initialValue;
		sliderButton = new ColorButton("");
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(SkinManager.skinColor));
		buttonBorder.setSize(2);
		sliderButton.setBorder(buttonBorder);
		sliderButton.setColor(Color.BLACK);
		sliderButton.setLabelColor(new Color(SkinManager.skinColor));
		sliderButton.setHoverColor(new Color(SkinManager.darkerSkinColor));
		sliderButton.setDownColor(new Color(SkinManager.lighterSkinColor));
		labelCharCount = 3;
	}
	
	@Override
	protected float calculateWidth() {
		return getBounds().width;
	}
	
	@Override
	protected float calculateHeight() {
		return getBounds().height;
	}
	
	@Override
	protected void updateComponent() {
		sliderButton.setFixedHeight(getBounds().height);
		sliderButton.setFixedWidth(getBounds().height);
		sliderButton.setAlpha(getAlpha());
		if (sliderButton.isDown()) {
			if (lastUpdateMousePosition != null) {
				Vector2 thisUpdateMousePosition = InputUtil.getNormalizedMousePosition();
				float deltaX = thisUpdateMousePosition.x - lastUpdateMousePosition.x;
				currentValue = MathUtil.clamp(currentValue + (deltaX / (getMaxButtonXPos() - getBounds().x)), 0, 1f);
				valueChangeAction.execute(currentValue);
			}
			lastUpdateMousePosition = InputUtil.getNormalizedMousePosition();
		} else {
			if (lastUpdateMousePosition != null) {
				if (dragEndAction != null) {
					dragEndAction.execute();
				}
				lastUpdateMousePosition = null;
			}
		}
		float sliderXPos = MathUtil.linear(getBounds().x, getMaxButtonXPos(), currentValue);
		sliderButton.setPosition(sliderXPos, getBounds().y);
	}
	
	@Override
	protected void renderComponent() {
		Rectangle sliderLineRect = new Rectangle(getScreenScaleAdjustedBounds().x, getScreenScaleAdjustedBounds().y + (getScreenScaleAdjustedBounds().height / 2) + 1, getScreenScaleAdjustedBounds().width, 2);
		Color sliderLineColor = new Color(SkinManager.darkerSkinColor);
		sliderLineColor.a *= getAlpha();
		SimpleShapeRenderer.drawRectangle(sliderLineRect, sliderLineColor);
		sliderButton.render();
	}

	private float getMaxButtonXPos() {
		return getBounds().x + getBounds().width - sliderButton.getFixedWidth();
	}

	@Override
	public void dispose() {
		sliderButton.dispose();
	}

}
