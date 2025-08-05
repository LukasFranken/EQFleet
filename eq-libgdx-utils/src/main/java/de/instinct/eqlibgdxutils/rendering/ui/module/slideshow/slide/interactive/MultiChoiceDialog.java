package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideButton;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
public class MultiChoiceDialog extends Slide {
	
	private float LABEL_MOVE_DURATION = 0.5f;
	private float LABEL_MAX_OFFSET = 20f;
	private float DIALOG_FADE_IN_DELAY = 2f;
	private float BUTTON_SPACING = 20f;
	private float BUTTON_MARGIN = 40f;
	
	private Label label;
	private List<SlideButton> choices;
	
	@Getter
	private List<ColorButton> buttons;
	
	public MultiChoiceDialog(String message) {
		label = new Label(message);
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
		buttonBorder.setColor(new Color(SkinManager.skinColor));
		buttonBorder.setSize(2);

		ColorButton slideButton = new ColorButton(label);
		slideButton.setBorder(buttonBorder);
		slideButton.setColor(Color.BLACK);
		slideButton.setFixedWidth(GraphicsUtil.baseScreenBounds().width - (BUTTON_MARGIN * 2));
		slideButton.setFixedHeight(40);
		slideButton.setLabelColor(new Color(SkinManager.skinColor));
		slideButton.setHoverColor(new Color(SkinManager.darkerSkinColor));
		slideButton.setDownColor(new Color(SkinManager.lighterSkinColor));
		return slideButton;
	}
	
	@Override
	protected void updateSlide(float slideAlpha) {
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
			button.setPosition(BUTTON_MARGIN, GraphicsUtil.baseScreenBounds().height / 2 - button.getFixedHeight() - (i * (button.getFixedHeight() + BUTTON_SPACING)));
			button.setAlpha(buttonAlpha);
			i++;
		}
	}
	
	@Override
	public void renderContent() {
		label.render();
		for (ColorButton button : buttons) {
			button.render();
		}
	}

}
