package de.instinct.eqfleet.menu.module.inventory;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultLabelFactory;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;

public class InventoryRenderer extends BaseModuleRenderer {
	
	private List<ElementStack> resourceStacks;
	
	public InventoryRenderer() {
		resourceStacks = new ArrayList<>();
	}

	@Override
	public void render() {
		if (resourceStacks != null) {
			for (ElementStack resourceStack : resourceStacks) {
				resourceStack.render();
			}
		}
	}

	@Override
	public void reload() {
		if (InventoryModel.resources != null) {
			float step = 20f;
			int i = 1;
			for (ResourceAmount resource : InventoryModel.resources.getResources()) {
				ElementStack resourceStack = DefaultLabelFactory.createResourceStack(resource);
				resourceStack.setFixedWidth(MenuModel.moduleBounds.width - 40);
				resourceStack.setPosition(MenuModel.moduleBounds.x + 20, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 10 - (i * step));
				resourceStacks.add(resourceStack);
				i++;
			}
		}
	}

	@Override
	public void dispose() {
		
	}

}
