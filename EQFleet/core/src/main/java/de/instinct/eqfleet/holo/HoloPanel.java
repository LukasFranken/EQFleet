package de.instinct.eqfleet.holo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.holo.style.HoloPanelStyle;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class HoloPanel {
	
	@Default
	private Rectangle bounds = new Rectangle();
	
	@Default
	private Color color = new Color();
	
	@Default
	private float elapsed = 0f;
	
	@Default
	private HoloPanelStyle style = HoloPanelStyle.builder().build();
	
	public void setBounds(float x, float y, float width, float height) {
		bounds.set(x, y, width, height);
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds.set(bounds);
	}
	
	public void setColor(Color color) {
		this.color.set(color);
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}

}
