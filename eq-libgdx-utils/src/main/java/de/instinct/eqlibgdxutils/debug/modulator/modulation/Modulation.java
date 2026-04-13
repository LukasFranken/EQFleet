package de.instinct.eqlibgdxutils.debug.modulator.modulation;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public abstract class Modulation {
	
	private Label tagLabel;
	
	private Rectangle modulationBounds;
	
	public Modulation(String tag) {
		modulationBounds = new Rectangle();
		tagLabel = new Label(tag);
		tagLabel.setType(FontType.SMALL);
		tagLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
	}
	
	public void render(Rectangle bounds) {
		modulationBounds.set(bounds);
		
		tagLabel.setBounds(modulationBounds.x, modulationBounds.y, 80f, modulationBounds.height);
		tagLabel.render();
		
		modulationBounds.x += 80f;
		modulationBounds.width -= 80f;
		renderModulation(modulationBounds);
	}
	
	public abstract void renderModulation(Rectangle bounds);

}
