package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class InteractiveSlide extends Slide {
	
	private ColorButton backButton;
	private boolean backButtonEnabled;
	
	public InteractiveSlide() {
		super();
		backButtonEnabled = true;
	}
	
	@Override
	protected void initSlide() {
		backButton = createSlideButton("Back");
		backButton.setFixedWidth(80);
		backButton.setAction(new Action() {
			
			@Override
			public void execute() {
				setBack(true);
			}
			
		});
		initInteractiveSlide();
	}
	
	protected abstract void initInteractiveSlide();

	@Override
	protected void updateSlide(float slideAlpha) {
		backButton.setPosition(GraphicsUtil.screenBounds().width / 2 - (backButton.getFixedWidth() / 2), 100);
		backButton.setAlpha(slideAlpha);
		updateInteractiveSlide(slideAlpha);
	}
	
	protected abstract void updateInteractiveSlide(float slideAlpha);
	
	@Override
	public void renderContent() {
		if (backButtonEnabled) {
			backButton.render();
		}
		renderInteractiveSlideContent();
	}

	protected abstract void renderInteractiveSlideContent();
	
	protected ColorButton createSlideButton(String label) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(SkinManager.skinColor));
		buttonBorder.setSize(2);

		ColorButton slideButton = new ColorButton(label);
		slideButton.setBorder(buttonBorder);
		slideButton.setColor(Color.BLACK);
		slideButton.setFixedWidth(80);
		slideButton.setFixedHeight(40);
		slideButton.setLabelColor(new Color(SkinManager.skinColor));
		slideButton.setHoverColor(new Color(SkinManager.darkerSkinColor));
		slideButton.setDownColor(new Color(SkinManager.lighterSkinColor));
		return slideButton;
	}
	
}
