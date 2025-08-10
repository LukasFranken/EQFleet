package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.Slider;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.ValueChangeAction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.InteractiveSlide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideAction;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SliderSlide extends InteractiveSlide {
	
	private float LABEL_MOVE_DURATION = 0.5f;
	private float LABEL_MAX_OFFSET = 20f;
	private float DIALOG_FADE_IN_DELAY = 2f;
	
	private String message;
	private Action acceptAction;
	private ValueChangeAction valueChangeAction;
	private float initialValue;
	
	private Label messageLabel;
	private ColorButton acceptButton;
	private Slider slider;
	
	public SliderSlide(String message, ValueChangeAction valueChangeAction, float initialValue, Action acceptAction) {
		super();
		this.message = message;
		this.acceptAction = acceptAction;
		this.valueChangeAction = valueChangeAction;
		this.initialValue = initialValue;
	}
	
	@Override
	protected void initInteractiveSlide() {
		messageLabel = new Label(message);
		acceptButton = createSlideButton("Confirm");
		acceptButton.setFixedWidth(80);
		SlideAction slideAcceptAction = new SlideAction() {
			boolean triggered = false;
			
			@Override
			public void execute() {
				if (!triggered) {
					acceptAction.execute();
				}
				triggered = true;
			}
			
			@Override
			public boolean executed() {
				return triggered;
			}
			
		};
		acceptButton.setAction(slideAcceptAction);
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return acceptAction == null ? true : !slideAcceptAction.executed();
			}

		});
		slider = new Slider(valueChangeAction, initialValue);
		slider.setFixedWidth(getBounds().width / 2);
		slider.setFixedHeight(40);
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
		
		slider.setPosition((getBounds().width / 2) - (slider.getBounds().width / 2), (getBounds().height / 2) - acceptButton.getBounds().height);
		slider.setAlpha(buttonAlpha);
		
		acceptButton.setBounds(new Rectangle((getBounds().width / 2) - (acceptButton.getBounds().width / 2),slider.getBounds().y - acceptButton.getBounds().height - 30, acceptButton.getBounds().width, acceptButton.getBounds().height));
		acceptButton.setAlpha(buttonAlpha);
		
		messageLabel.setBounds(new Rectangle(0, labelYOffset, getBounds().width, getBounds().height));
		messageLabel.setAlpha(slideAlpha);
	}

	@Override
	protected void renderInteractiveSlideContent() {
		slider.render();
		messageLabel.render();
		acceptButton.render();
	}

}
