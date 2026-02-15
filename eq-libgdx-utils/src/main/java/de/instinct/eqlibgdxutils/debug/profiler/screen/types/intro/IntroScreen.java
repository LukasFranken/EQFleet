package de.instinct.eqlibgdxutils.debug.profiler.screen.types.intro;

import de.instinct.eqlibgdxutils.debug.profiler.ProfilerModel;
import de.instinct.eqlibgdxutils.debug.profiler.screen.Screen;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class IntroScreen extends Screen {
	
	private Label label;
	
	public IntroScreen() {
		label = new Label("~ Profiler Ready ~");
		label.setType(FontType.LARGE);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void renderButtons() {
		
	}

	@Override
	public void renderContent() {
		label.setBounds(ProfilerModel.screenBounds);
		label.render();
	}

}
