package de.instinct.eqfleet.menu.main.header.components.social;

import de.instinct.eqfleet.menu.module.social.SocialModel;
import de.instinct.eqfleet.menu.module.social.message.RespondToFriendRequestMessage;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;

public class FriendsSocialHeaderComponent extends Component {
	
	private MiniWindow friendRequestWindow;
	
	public FriendsSocialHeaderComponent() {
		friendRequestWindow = new MiniWindow(MiniWindowConfiguration.builder()
				.title("FRIEND REQUEST")
				.content("")
				.acceptButtonText("ACCEPT")
				.acceptAction(new Action() {
					
					@Override
					public void execute() {
						SocialModel.messageQueue.add(RespondToFriendRequestMessage.builder()
								.requestId(0)
								.accept(true)
								.build());
					}
					
				})
				.declineButtonText("X")
				.declineAction(new Action() {
					
					@Override
					public void execute() {
						SocialModel.messageQueue.add(RespondToFriendRequestMessage.builder()
								.requestId(0)
								.accept(false)
								.build());
					}
					
				})
				.build());
	}
	
	@Override
	protected void updateComponent() {
		friendRequestWindow.setBounds(getBounds());
		
		if (SocialModel.playerData.getFriendRequests().size() > 0) {
			friendRequestWindow.getContentLabel().setText(SocialModel.playerData.getFriendRequests().get(0).getFromName());
		}
	}
	
	@Override
	protected void renderComponent() {
		if (SocialModel.playerData.getFriendRequests().size() > 0) {
			friendRequestWindow.render();
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
		friendRequestWindow.dispose();
	}

}
