package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow;

import java.util.Queue;

import de.instinct.eqlibgdxutils.rendering.ui.module.BaseModule;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Slideshow extends BaseModule {

	private Queue<Slide> elementQueue;
	private Slide currentElement;

	public Slideshow() {
		setDecorated(false);
	}

	@Override
	public void updatePosition() {
		getBounds().x = 0;
		getBounds().y = 0;
	}

	@Override
	public void updateContent() {

	}

	@Override
	public void updateContentPosition() {

	}

	@Override
	protected void renderContent() {
		if (currentElement == null && elementQueue != null) {
			currentElement = elementQueue.poll();
		}
		if (currentElement != null) {
			currentElement.setBounds(getBounds());
			currentElement.render();
			if (currentElement.getStage() == SlideLifeCycleStage.FINISHED) {
				currentElement = null;
			}
		}
	}

	@Override
	protected float calculateWidth() {
		return getBounds().width;
	}

	@Override
	protected float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public void dispose() {

	}

}
