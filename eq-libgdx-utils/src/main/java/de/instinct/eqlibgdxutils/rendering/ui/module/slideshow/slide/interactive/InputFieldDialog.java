package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.InteractiveSlide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
public class InputFieldDialog extends InteractiveSlide {

	private float LABEL_MOVE_DURATION = 0.5f;
	private float LABEL_MAX_OFFSET = 20f;
	private float DIALOG_FADE_IN_DELAY = 2f;
	
	private Label mainLabel;
	private Label subLabel;

	@Getter
	private LimitedInputField inputField;

	public InputFieldDialog(String message, String subMessage) {
		super();
		mainLabel = new Label(message);
		subLabel = new Label(subMessage);
	}
	
	@Override
	protected void initInteractiveSlide() {
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return true;
			}

		});
		inputField = new LimitedInputField();
		LABEL_MAX_OFFSET *= GraphicsUtil.getHorizontalDisplayScaleFactor();
		inputField.focus();
	}
	
	@Override
	protected void updateInteractiveSlide(float slideAlpha) {
		float labelYOffset = 0f;
		float buttonAlpha = 0f;
		if (getStage() == SlideLifeCycleStage.FADE_OUT) {
			labelYOffset = LABEL_MAX_OFFSET;
			buttonAlpha = slideAlpha;
		}
		if (getStage() == SlideLifeCycleStage.ACTIVE) {
			if (getStageElapsed() >= DIALOG_FADE_IN_DELAY - LABEL_MOVE_DURATION) {
				labelYOffset = MathUtil.linear(0, LABEL_MAX_OFFSET, (getStageElapsed() - (DIALOG_FADE_IN_DELAY - LABEL_MOVE_DURATION)) / getFADE_IN_DURATION());
			}
			if (getStageElapsed() >= DIALOG_FADE_IN_DELAY) {
				buttonAlpha = MathUtil.linear(0, 1, (getStageElapsed() - DIALOG_FADE_IN_DELAY) / getFADE_IN_DURATION());
			}
		}
		inputField.setBounds(new Rectangle((getBounds().width / 2) - (inputField.getBounds().width / 2), (getBounds().height / 2) - inputField.getBounds().height, inputField.getBounds().width, inputField.getBounds().height));
		inputField.setAlpha(buttonAlpha);
		inputField.update();
		
		mainLabel.setBounds(new Rectangle(0, labelYOffset, getBounds().width, getBounds().height));
		mainLabel.setAlpha(slideAlpha);
		
		subLabel.setBounds(new Rectangle(0, inputField.getBounds().y - 40, getBounds().width, 30 * GraphicsUtil.getHorizontalDisplayScaleFactor()));
		subLabel.setAlpha(slideAlpha);
	}

	@Override
	public void renderInteractiveSlideContent() {
		inputField.render();
		mainLabel.render();
		subLabel.render();
	}

}
