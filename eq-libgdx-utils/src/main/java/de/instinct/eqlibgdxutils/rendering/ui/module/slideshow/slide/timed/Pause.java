package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed;

import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.TimedSlide;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Pause extends TimedSlide {

	public Pause() {
		super();
	}
	
	@Override
	protected void initTimedSlide() {
		setFade(false);
	}
	
	@Override
	protected void updateSlide(float slideAlpha) {}

	@Override
	public void renderContent() {}

}
