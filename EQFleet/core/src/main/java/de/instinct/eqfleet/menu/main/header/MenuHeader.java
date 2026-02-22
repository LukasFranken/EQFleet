package de.instinct.eqfleet.menu.main.header;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.main.header.components.ProfileHeaderComponent;
import de.instinct.eqfleet.menu.main.header.components.ResourceHeaderComponent;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqfleet.menu.module.profile.inventory.InventoryModel;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;

public class MenuHeader extends Component {
	
	private ProfileHeaderComponent profileComponent;
	private ResourceHeaderComponent resourceComponent;
	
	public MenuHeader() {
		profileComponent = new ProfileHeaderComponent();
		resourceComponent = new ResourceHeaderComponent();
	}

	public void init() {
		profileComponent.init();
		resourceComponent.init();
	}
	
	@Override
	protected void updateComponent() {
		profileComponent.getBounds().set(getBounds().x, getBounds().y, getBounds().width / 2, getBounds().height);
		resourceComponent.getBounds().set(getBounds().x + (getBounds().width / 2), getBounds().y, getBounds().width / 2, getBounds().height);
		
		profileComponent.setAlpha(getAlpha());
		resourceComponent.setAlpha(getAlpha());
	}
	
	@Override
	protected void renderComponent() {
		if (ProfileModel.profile != null && MenuModel.unlockedModules.getEnabledModules().contains(MenuModule.PROFILE)) {
			if (MenuModel.activeModule != MenuModule.PROFILE) {
				profileComponent.render();
			}
		}
		if (InventoryModel.resources != null && MenuModel.unlockedModules.getEnabledModules().contains(MenuModule.PROFILE)) {
			if (MenuModel.activeModule != MenuModule.PROFILE) {
				resourceComponent.render();
			}
		}
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		profileComponent.dispose();
		resourceComponent.dispose();
	}

}
