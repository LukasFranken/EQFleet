package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.InteractiveSlide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideAction;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
public class BinaryLabeledDialog extends InteractiveSlide {

	private float LABEL_MOVE_DURATION = 0.5f;
	private float LABEL_MAX_OFFSET = 20f;
	private float DIALOG_FADE_IN_DELAY = 1.5f;

	private Label messageLabel;
	
	private boolean triggered;
	private String acceptLabel;
	private String denyLabel;
	private Action acceptAction;
	private Action denyAction;

	@Getter
	private ColorButton acceptButton;
	@Getter
	private ColorButton denyButton;
	
	private String message;

	public BinaryLabeledDialog(String message, String acceptLabel, String denyLabel, Action acceptAction, Action denyAction) {
		super();
		this.message = message;
		this.acceptLabel = acceptLabel;
		this.denyLabel = denyLabel;
		this.acceptAction = acceptAction;
		this.denyAction = denyAction;
	}
	
	@Override
	protected void initInteractiveSlide() {
		messageLabel = new Label(message);
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
		SlideAction slideDenyAction = new SlideAction() {
			boolean triggered = false;
			
			@Override
			public void execute() {
				if (!triggered) {
					denyAction.execute();
				}
				triggered = true;
			}
			
			@Override
			public boolean executed() {
				return triggered;
			}
			
		};
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return acceptAction == null ? true : !slideAcceptAction.executed();
			}

		});
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return denyAction == null ? true : !slideDenyAction.executed();
			}

		});
		acceptButton = createSlideButton(acceptLabel);
		acceptButton.setAction(slideAcceptAction);

		denyButton = createSlideButton(denyLabel);
		denyButton.setAction(slideDenyAction);
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
		acceptButton.setBounds(new Rectangle((getBounds().width / 2) - acceptButton.getBounds().width - 10, (getBounds().height / 2) - acceptButton.getBounds().height, acceptButton.getBounds().width, acceptButton.getBounds().height));
		denyButton.setBounds(new Rectangle((getBounds().width / 2) + 10, (getBounds().height / 2) - denyButton.getBounds().height, denyButton.getBounds().width, denyButton.getBounds().height));
		acceptButton.setAlpha(buttonAlpha);
		denyButton.setAlpha(buttonAlpha);
		
		messageLabel.setBounds(new Rectangle(0, labelYOffset, getBounds().width, getBounds().height));
		messageLabel.setAlpha(slideAlpha);
	}

	@Override
	public void renderInteractiveSlideContent() {
		acceptButton.render();
		denyButton.render();
		messageLabel.render();
	}

}
