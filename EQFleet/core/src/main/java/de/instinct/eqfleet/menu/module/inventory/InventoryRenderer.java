package de.instinct.eqfleet.menu.module.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class InventoryRenderer extends BaseModuleRenderer {

	@Override
	public void render() {
		if (InventoryModel.resources != null) {
			Label creditsLabel = new Label("Credits: " + InventoryModel.resources.getCredits());
			creditsLabel.setBounds(new Rectangle(0, 500, Gdx.graphics.getWidth(), 20));
			creditsLabel.render();
			
			Label ironLabel = new Label("Iron: " + InventoryModel.resources.getIron());
			ironLabel.setBounds(new Rectangle(0, 480, Gdx.graphics.getWidth(), 20));
			ironLabel.render();
			
			Label goldLabel = new Label("Gold: " + InventoryModel.resources.getGold());
			goldLabel.setBounds(new Rectangle(0, 460, Gdx.graphics.getWidth(), 20));
			goldLabel.render();
			
			Label quartzLabel = new Label("Quartz: " + InventoryModel.resources.getQuartz());
			quartzLabel.setBounds(new Rectangle(0, 440, Gdx.graphics.getWidth(), 20));
			quartzLabel.render();
			
			Label deuteriumLabel = new Label("Deuterium: " + InventoryModel.resources.getDeuterium());
			deuteriumLabel.setBounds(new Rectangle(0, 420, Gdx.graphics.getWidth(), 20));
			deuteriumLabel.render();
			
			Label equilibriumLabel = new Label("Equilibrium: " + InventoryModel.resources.getEquilibrium());
			equilibriumLabel.setBounds(new Rectangle(0, 400, Gdx.graphics.getWidth(), 20));
			equilibriumLabel.render();
		}
	}

	@Override
	public void reload() {
		
	}

	@Override
	public void dispose() {
		
	}

}
