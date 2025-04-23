package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class AnimatedLabel extends Label {
	
	private ColorHook colorHook;

	public AnimatedLabel(CharSequence text, LabelStyle style, ColorHook colorHook) {
		super(text, style);
		this.colorHook = colorHook;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		this.getStyle().fontColor = colorHook.getColor();
        super.draw(batch, parentAlpha);
    }

}
