package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Macro extends Slide {

	private List<Action> actions;
	private boolean executed;

	public Macro() {
		super();
		actions = new ArrayList<>();
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
		for (Action action : actions) {
			action.execute();
		}
		executed = true;
	}

	@Override
	public void renderContent() {
		
	}

	public void setAction(Action action) {
		actions.add(action);
	}

}
