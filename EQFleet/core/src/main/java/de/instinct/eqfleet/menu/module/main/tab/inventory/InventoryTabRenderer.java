package de.instinct.eqfleet.menu.module.main.tab.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.meta.dto.ResourceData;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class InventoryTabRenderer extends BaseModuleRenderer {
	
	public InventoryTabRenderer() {
		
	}

	@Override
	public void render() {
		/*if (GlobalStaticData.profile != null && GlobalStaticData.profile.getResources() != null) {
			ResourceData resources = GlobalStaticData.profile.getResources();
			Label creditsLabel = new Label("Credits: " + resources.getCredits());
			creditsLabel.setBounds(new Rectangle(0, 500, Gdx.graphics.getWidth(), 20));
			creditsLabel.render();
			
			Label ironLabel = new Label("Iron: " + resources.getIron());
			ironLabel.setBounds(new Rectangle(0, 480, Gdx.graphics.getWidth(), 20));
			ironLabel.render();
			
			Label goldLabel = new Label("Gold: " + resources.getGold());
			goldLabel.setBounds(new Rectangle(0, 460, Gdx.graphics.getWidth(), 20));
			goldLabel.render();
			
			Label quartzLabel = new Label("Quartz: " + resources.getQuartz());
			quartzLabel.setBounds(new Rectangle(0, 440, Gdx.graphics.getWidth(), 20));
			quartzLabel.render();
			
			Label deuteriumLabel = new Label("Deuterium: " + resources.getDeuterium());
			deuteriumLabel.setBounds(new Rectangle(0, 420, Gdx.graphics.getWidth(), 20));
			deuteriumLabel.render();
			
			Label equilibriumLabel = new Label("Equilibrium: " + resources.getEquilibrium());
			equilibriumLabel.setBounds(new Rectangle(0, 400, Gdx.graphics.getWidth(), 20));
			equilibriumLabel.render();
		}*/
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

}
