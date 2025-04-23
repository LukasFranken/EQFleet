package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideButton;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
public class MultiChoiceDialog extends Slide {
	
	private final float LABEL_MOVE_DURATION = 0.5f;
	private final float LABEL_MAX_OFFSET = 20f;
	private final float DIALOG_FADE_IN_DELAY = 2f;
	private final float BUTTON_SPACING = 20f;
	private final float BUTTON_MARGIN = 40f;
	
	private String message;
	private List<SlideButton> choices;
	
	@Getter
	private List<ColorButton> buttons;
	
	public MultiChoiceDialog(String message) {
		this.message = message;
		choices = new ArrayList<>();
		
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				for (SlideButton choice : choices) {
					if (choice.getAction().executed()) {
						return false;
					}
				}
				return true;
			}

		});
	}
	
	public void build() {
		buttons = new ArrayList<>();
		for (SlideButton choice : choices) {
			ColorButton newChoiceButton = createSlideButton(choice.getLabelText());
			newChoiceButton.setAction(choice.getAction());
			buttons.add(newChoiceButton);
		}
	}
	
	private ColorButton createSlideButton(String label) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
		buttonBorder.setSize(2);

		ColorButton slideButton = new ColorButton(label);
		slideButton.setBorder(buttonBorder);
		slideButton.setColor(Color.BLACK);
		slideButton.setFixedWidth(Gdx.graphics.getWidth() - (BUTTON_MARGIN * 2));
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
		
		FontUtil.drawLabel(message, new Rectangle(0, labelYOffset, getBounds().width, getBounds().height), slideAlpha);
		
		int i = 0;
		for (ColorButton button : buttons) {
			button.setPosition(BUTTON_MARGIN, Gdx.graphics.getHeight() / 2 - button.getFixedHeight() - (i * (button.getFixedHeight() + BUTTON_SPACING)));
			button.setAlpha(buttonAlpha);
			button.render();
			i++;
		}
	}

}
