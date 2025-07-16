package de.instinct.eqfleet.menu.module.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class InventoryRenderer extends BaseModuleRenderer {

	@Override
	public void render() {
		if (InventoryModel.resources != null) {
			float step = 20f;
			int i = 0;
			for (ResourceAmount resource : InventoryModel.resources.getResources()) {
				Label resourceLabel = new Label(resource.getType().toString() + ": " + resource.getAmount());
				resourceLabel.setBounds(new Rectangle(0, 500 - (step * i), Gdx.graphics.getWidth(), 20));
				resourceLabel.render();
				i++;
			}
		}
	}

	@Override
	public void reload() {
		
	}

	@Override
	public void dispose() {
		
	}

}
