package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.TimedSlide;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Message extends TimedSlide {

	private String message;

	@Override
	public void renderContent(float slideAlpha) {
		FontUtil.drawLabel(message, new Rectangle(0, 0, getBounds().width, getBounds().height), slideAlpha);
	}

}
