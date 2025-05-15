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
	
	public Message(String message) {
		label = new Label(message);
		label.setBounds(new Rectangle(0, 0, getBounds().width, getBounds().height));
	}

	@Override
	public void renderContent(float slideAlpha) {
		label.setAlpha(slideAlpha);
		label.render();
	}

}
