package de.instinct.eqfleet.mining.frontend;

import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;

public class MiningHudRenderer {
	
	private BoxedRectangularLoadingBar bar;
	
	public void init() {
		Border barBorder = new Border();
		barBorder.setColor(GameConfig.teammate1Color);
		barBorder.setSize(1f);
		
		bar = new BoxedRectangularLoadingBar();
		bar.setFixedHeight(10f);
		bar.setBorder(barBorder);
	}
	
	public void update() {
		bar.setSegments(10);
		bar.setMaxValue(10);
		bar.setCurrentValue(10);
		bar.setBounds(20, GraphicsUtil.screenBounds().height - 20, 160, 6);
	}
	
	public void render() {
		bar.render();
	}
	
	public void dispose() {
		bar.dispose();
	}

}
