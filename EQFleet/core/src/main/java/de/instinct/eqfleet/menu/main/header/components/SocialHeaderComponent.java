package de.instinct.eqfleet.menu.main.header.components;

import de.instinct.eqfleet.menu.main.header.components.social.FriendsSocialHeaderComponent;
import de.instinct.eqfleet.menu.main.header.components.social.GroupSocialHeaderComponent;
import de.instinct.eqfleet.menu.module.social.SocialModel;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;

public class SocialHeaderComponent extends Component {
	
	private FriendsSocialHeaderComponent friendsComponent;
	private GroupSocialHeaderComponent groupComponent;
	
	public SocialHeaderComponent() {
		friendsComponent = new FriendsSocialHeaderComponent();
		groupComponent = new GroupSocialHeaderComponent();
	}

	@Override
	protected void updateComponent() {
		friendsComponent.setBounds(getBounds().x, getBounds().y, getBounds().width / 2, getBounds().height);
		groupComponent.setBounds(getBounds().x + (getBounds().width / 2), getBounds().y, getBounds().width / 2, getBounds().height);
	}
	
	@Override
	protected void renderComponent() {
		if (SocialModel.playerData != null) {
			friendsComponent.render();
			groupComponent.render();
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
		friendsComponent.dispose();
		groupComponent.dispose();
	}
	
}
