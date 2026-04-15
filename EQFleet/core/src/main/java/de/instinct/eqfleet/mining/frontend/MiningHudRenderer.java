package de.instinct.eqfleet.mining.frontend;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.mining.MiningModel;
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
		MiningPlayerShip ship = MiningModel.state.entityData.playerShips.get(0);
		bar.setSegments(10);
		bar.setMaxValue(ship.core.maxCharge);
		bar.setCurrentValue(ship.core.currentCharge);
		bar.setPartialSegments(true);
		bar.setBounds(20, GraphicsUtil.screenBounds().height - 80, 160, 6);
	}
	
	public void render() {
		bar.render();
	}
	
	public void dispose() {
		bar.dispose();
	}

}
