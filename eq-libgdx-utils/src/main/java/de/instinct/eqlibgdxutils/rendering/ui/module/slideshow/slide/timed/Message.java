package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.TimedSlide;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Message extends TimedSlide {

	private Label label;
	private String message;
	
	public Message(String message) {
		super();
		this.message = message;
	}
	
	@Override
	protected void initTimedSlide() {
		label = new Label(message);
		label.setBounds(new Rectangle(0, 0, getBounds().width, getBounds().height));
	}
	
	@Override
	protected void updateSlide(float slideAlpha) {
		label.setAlpha(slideAlpha);
	}

	@Override
	public void renderContent() {
		if (label != null && label.getText() != null) {
			label.render();
		}
	}

}
