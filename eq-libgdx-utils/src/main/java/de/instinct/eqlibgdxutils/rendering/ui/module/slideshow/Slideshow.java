package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.rendering.ui.module.BaseModule;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Slideshow extends BaseModule {

	private List<Slide> slideList;
	private int currentElement;

	public Slideshow() {
		setDecorated(false);
		currentElement = 0;
		slideList = new ArrayList<>();
	}

	@Override
	public void updatePosition() {
		getBounds().x = 0;
		getBounds().y = 0;
	}

	@Override
	public void updateContent() {
		if (currentElement < slideList.size() && slideList.get(currentElement) != null) {
			slideList.get(currentElement).setBounds(getBounds());
			if (slideList.get(currentElement).getStage() == SlideLifeCycleStage.FINISHED) {
				if (!slideList.get(currentElement).isBack()) {
					currentElement++;
				} else {
					currentElement--;
					slideList.get(currentElement).init();
					slideList.remove(slideList.size() - 1);
				}
			}
		}
	}
	
	public void add(Slide slide) {
		slide.init();
		slideList.add(slide);
	}

	@Override
	public void updateContentPosition() {

	}

	@Override
	protected void renderContent() {
		if (currentElement < slideList.size() && slideList.get(currentElement) != null) slideList.get(currentElement).render();
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
