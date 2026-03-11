package de.instinct.eqfleet.menu.module.profile.inventory;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.meta.dto.Resource;
import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.label.DefaultLabelFactory;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.profile.ProfileModuleAPI;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;

public class InventoryRenderer extends BaseModuleRenderer {
	
	private List<ElementStack> resourceStacks;
	
	public InventoryRenderer() {
		resourceStacks = new ArrayList<>();
	}

	@Override
	public void init() {
		resourceStacks.clear();
		for (Resource type : Resource.values()) {
			ResourceAmount resource = new ResourceAmount();
			resource.setType(type);
			ElementStack resourceStack = DefaultLabelFactory.createResourceStack(resource);
			resourceStacks.add(resourceStack);
		}
	}
	
	@Override
	public void update() {
		float step = 20f;
		int i = 1;
		for (ElementStack resourceStack : resourceStacks) {
			long amount = ProfileModuleAPI.getResource(Resource.valueOf(((Label)resourceStack.getElements().get(0)).getText()));
			((Label)resourceStack.getElements().get(1)).setText(StringUtils.formatBigNumber(Math.abs(amount)));
			if (amount > 0) {
				resourceStack.setFixedWidth(MenuModel.moduleBounds.width - 40);
				resourceStack.setPosition(MenuModel.moduleBounds.x + 20, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - 10 - (i * step));
				i++;
			}
		}
	}
	
	@Override
	public void render() {
		if (resourceStacks != null) {
			for (ElementStack resourceStack : resourceStacks) {
				if (!((Label)resourceStack.getElements().get(1)).getText().contentEquals("0")) {
					resourceStack.render();
				}
			}
		}
	}

	@Override
	public void dispose() {
		for (ElementStack resourceStack : resourceStacks) {
			resourceStack.dispose();
		}
	}

}
