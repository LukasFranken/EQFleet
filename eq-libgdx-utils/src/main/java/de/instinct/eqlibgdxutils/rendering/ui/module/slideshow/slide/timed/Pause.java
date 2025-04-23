package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed;

import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.TimedSlide;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Pause extends TimedSlide {

	public Pause() {
		super();
		setFade(false);
	}

	@Override
	public void renderContent(float slideAlpha) {}

}
