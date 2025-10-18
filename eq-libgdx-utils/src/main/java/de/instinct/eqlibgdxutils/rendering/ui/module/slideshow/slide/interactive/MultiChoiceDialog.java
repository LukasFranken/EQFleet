package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.InteractiveSlide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideAction;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideChoice;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
public class MultiChoiceDialog extends InteractiveSlide {
	
	private float LABEL_MOVE_DURATION = 0.5f;
	private float LABEL_MAX_OFFSET = 20f;
	private float DIALOG_FADE_IN_DELAY = 2f;
	private float BUTTON_SPACING = 20f;
	private float BUTTON_MARGIN = 40f;
	
	private Label label;
	private List<SlideChoice> choices;
	
	@Getter
	private List<ColorButton> buttons;
	
	private String message;
	
	public MultiChoiceDialog(String message, List<SlideChoice> choices) {
		super();
		this.message = message;
		this.choices = choices;
		label = new Label(message);
	}
	
	@Override
	protected void initInteractiveSlide() {
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				for (ColorButton button : buttons) {
					if (((SlideAction)button.getAction()).executed()) {
						return false;
					}
				}
				return true;
			}

		});
		buttons = new ArrayList<>();
		for (SlideChoice choice : choices) {
			ColorButton newChoiceButton = createSlideButton(choice.getLabelText());
			newChoiceButton.setFixedWidth(GraphicsUtil.screenBounds().width - (BUTTON_MARGIN * 2));
			newChoiceButton.setAction(new SlideAction() {
				boolean triggered = false;
				
				@Override
				public void execute() {
					if (!triggered) {
						choice.getAction().execute();
					}
					triggered = true;
				}
				
				@Override
				public boolean executed() {
					return triggered;
				}
				
			});
			buttons.add(newChoiceButton);
		}
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
		label.setBounds(new Rectangle(0, labelYOffset, getBounds().width, getBounds().height));
		label.setAlpha(slideAlpha);
		
		int i = 0;
		for (ColorButton button : buttons) {
			button.setPosition(BUTTON_MARGIN, GraphicsUtil.screenBounds().height / 2 - button.getFixedHeight() - (i * (button.getFixedHeight() + BUTTON_SPACING)));
			button.setAlpha(buttonAlpha);
			i++;
		}
	}
	
	@Override
	public void renderInteractiveSlideContent() {
		label.render();
		for (ColorButton button : buttons) {
			button.render();
		}
	}

}
