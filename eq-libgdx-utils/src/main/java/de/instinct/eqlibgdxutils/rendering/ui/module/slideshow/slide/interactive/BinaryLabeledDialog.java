package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideAction;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
public class BinaryLabeledDialog extends Slide {

	private final float LABEL_MOVE_DURATION = 0.5f;
	private final float LABEL_MAX_OFFSET = 20f;
	private final float DIALOG_FADE_IN_DELAY = 2f;

	private String message;
	private boolean triggered;
	private String acceptLabel;
	private String denyLabel;
	private SlideAction acceptAction;
	private SlideAction denyAction;

	@Getter
	private ColorButton acceptButton;
	@Getter
	private ColorButton denyButton;

	public BinaryLabeledDialog() {
		super();
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return acceptAction == null ? true : !acceptAction.executed();
			}

		});
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return denyAction == null ? true : !denyAction.executed();
			}

		});
	}

	public void build() {
		acceptButton = createSlideButton(acceptLabel);
		acceptButton.setAction(acceptAction);

		denyButton = createSlideButton(denyLabel);
		denyButton.setAction(denyAction);
	}

	private ColorButton createSlideButton(String label) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
		buttonBorder.setSize(2);

		ColorButton slideButton = new ColorButton(label);
		slideButton.setBorder(buttonBorder);
		slideButton.setColor(Color.BLACK);
		slideButton.setFixedWidth(80);
		slideButton.setFixedHeight(40);
		slideButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		slideButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		slideButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		return slideButton;
	}

	@Override
	public void renderContent(float slideAlpha) {
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
		acceptButton.update();
		acceptButton.render();
		denyButton.update();
		denyButton.render();
		FontUtil.drawLabel(message, new Rectangle(0, labelYOffset, getBounds().width, getBounds().height), slideAlpha);
	}

}
