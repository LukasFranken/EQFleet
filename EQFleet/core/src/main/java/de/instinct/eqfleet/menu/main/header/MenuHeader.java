package de.instinct.eqfleet.menu.main.header;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.main.header.components.ProfileHeaderComponent;
import de.instinct.eqfleet.menu.main.header.components.SocialHeaderComponent;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;

public class MenuHeader extends Component {
	
	private ProfileHeaderComponent profileComponent;
	private SocialHeaderComponent socialComponent;
	
	public MenuHeader() {
		profileComponent = new ProfileHeaderComponent();
		socialComponent = new SocialHeaderComponent();
	}

	public void init() {
		profileComponent.init();
		socialComponent.init();
	}
	
	@Override
	protected void updateComponent() {
		profileComponent.getBounds().set(getBounds().x, getBounds().y, getBounds().width / 2, getBounds().height);
		socialComponent.getBounds().set(getBounds().x + (getBounds().width / 2), getBounds().y, getBounds().width / 2, getBounds().height);
		
		profileComponent.setAlpha(getAlpha());
		socialComponent.setAlpha(getAlpha());
	}
	
	@Override
	protected void renderComponent() {
		if (MenuModel.unlockedModules != null) {
			if (ProfileModel.profile != null && MenuModel.unlockedModules.getEnabledModules().contains(MenuModule.PROFILE)) {
				if (MenuModel.activeModule != MenuModule.PROFILE) {
					profileComponent.render();
				}
			}
			if (ProfileModel.resources != null && MenuModel.unlockedModules.getEnabledModules().contains(MenuModule.SOCIAL)) {
				if (MenuModel.activeModule != MenuModule.SOCIAL) {
					socialComponent.render();
				}
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
		socialComponent.dispose();
	}

}
