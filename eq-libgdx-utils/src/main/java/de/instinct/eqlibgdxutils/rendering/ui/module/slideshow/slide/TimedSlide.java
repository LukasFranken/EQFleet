package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide;

import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class TimedSlide extends Slide {

	private float duration;

	public TimedSlide() {
		super();
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return getStageElapsed() < duration;
			}

		});
	}

}
