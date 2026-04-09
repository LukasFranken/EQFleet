package de.instinct.eqfleet.mining;

import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.scene.Scene;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;

public class Mining extends Scene {
	
	private BoxedRectangularLoadingBar bar;
	
	private ShipRenderer shipRenderer;

	@Override
	public void init() {
		Border barBorder = new Border();
		barBorder.setColor(GameConfig.teammate1Color);
		barBorder.setSize(1f);
		
		bar = new BoxedRectangularLoadingBar();
		bar.setFixedHeight(10f);
		bar.setBorder(barBorder);
		
		shipRenderer = new ShipRenderer();
		shipRenderer.init();
	}

	@Override
	public void open() {
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public void update() {
		bar.setSegments(10);
		bar.setMaxValue(10);
		bar.setCurrentValue(10);
		bar.setBounds(20, GraphicsUtil.screenBounds().height - 20, 160, 6);
		
		shipRenderer.update();
	}

	@Override
	public void render() {
		shipRenderer.render();
		bar.render();
	}

	@Override
	public void dispose() {
		shipRenderer.dispose();
		bar.dispose();
	}

}
