package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide;

import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Macro extends Slide {

	private Action action;
	private boolean executed;

	public Macro(Action action) {
		super();
		this.action = action;
	}
	
	@Override
	protected void initSlide() {
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return !executed;
			}

		});
		setFade(false);
	}
	
	@Override
	protected void updateSlide(float slideAlpha) {
		action.execute();
		executed = true;
	}

	@Override
	public void renderContent() {
		
	}
	
}
