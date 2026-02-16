package de.instinct.eqlibgdxutils.debug.profiler.screen.element;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.debug.profiler.ProfilerModel;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;

public class ScreenButton {
	
	private int index;
	private ColorButton button;
	
	public ScreenButton(int index, String label, Action action) {
		this.index = index;
		
		button = new ColorButton(label);
		button.setConsoleBypass(true);
		button.setAction(action);
		button.setFixedWidth(40f);
		button.setFixedHeight(ProfilerModel.buttonHeight);
	}
	
	public void render() {
		Rectangle bounds = ProfilerModel.bounds;
		button.setPosition(bounds.x + bounds.width - button.getFixedWidth() - 10f - (index * 45f), bounds.y + 10f);
		button.render();
	}
	
}
