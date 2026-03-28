package de.instinct.eqfleet.menu.module.social;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.social.message.CreateGroupMessage;
import de.instinct.eqfleet.menu.module.social.message.FriendRequestMessage;
import de.instinct.eqfleet.menu.module.social.message.InviteToGroupMessage;
import de.instinct.eqfleet.menu.module.social.message.LeaveGroupMessage;
import de.instinct.eqfleet.menu.module.social.message.RemoveFriendMessage;
import de.instinct.eqfleet.menu.module.social.message.RespondToFriendRequestMessage;
import de.instinct.eqfleet.menu.module.social.message.RespondToInviteMessage;
import de.instinct.eqfleet.menu.module.social.message.SocialMessage;
import de.instinct.eqfleet.menu.module.starmap.StarmapModel;
import de.instinct.eqfleet.menu.module.starmap.message.types.ReloadStarmapMessage;
import de.instinct.eqfleet.net.WebManager;

public class Social extends BaseModule {
	
	private final int UPDATE_INTERVAL_MS = 1000;
	private long lastUpdateTime = 0;

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.SOCIAL;
	}

	@Override
	public void init() {
		SocialModel.messageQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void load() {
		loadData();
	}
	
	private void loadData() {
		lastUpdateTime = System.currentTimeMillis();
		WebManager.enqueue(
				() -> API.social().playerdata(API.authKey),
			    result -> {
			    	SocialModel.playerData = result;
			    	if (SocialModel.playerData != null && SocialModel.playerData.getGroupToken() != null) {
						WebManager.enqueue(
								() -> API.social().getGroup(SocialModel.playerData.getGroupToken()),
							    result2 -> {
							    	if (SocialModel.groupData == null || SocialModel.groupData.getMembers().size() != result2.getMembers().size()) {
							    		StarmapModel.messageQueue.add(ReloadStarmapMessage.builder().build());
							    	}
							    	SocialModel.groupData = result2;
							    }
						);
						
					} else {
						SocialModel.groupData = null;
					}
			    }
		);
		WebManager.enqueue(
				() -> API.social().pushData(API.authKey),
			    result -> {
			    	
			    }
		);
	}

	@Override
	public void update() {
		if (!SocialModel.messageQueue.isEmpty()) {
			process(SocialModel.messageQueue.poll());
		}
		
		if (System.currentTimeMillis() - lastUpdateTime >= UPDATE_INTERVAL_MS) {
			loadData();
		}
	}
	
	private void process(SocialMessage message) {
		if (message instanceof RespondToFriendRequestMessage) {
			if (SocialModel.playerData.getFriendRequests().size() <= 0) return;
			RespondToFriendRequestMessage friendRequestMessage = (RespondToFriendRequestMessage) message;
			WebManager.enqueue(
					() -> API.social().respondToFriendRequest(API.authKey, SocialModel.playerData.getFriendRequests().get(friendRequestMessage.getRequestId()).getFromName(), friendRequestMessage.isAccept()),
				    result -> {
				    	loadData();
				    }
			);
		}
		if (message instanceof RespondToInviteMessage) {
			if (SocialModel.playerData.getGroupInvites().size() <= 0) return;
			RespondToInviteMessage inviteMessage = (RespondToInviteMessage) message;
			WebManager.enqueue(
					() -> API.social().respondToGroupInvite(API.authKey, SocialModel.playerData.getGroupInvites().get(inviteMessage.getInviteId()).getGroupToken(), inviteMessage.isAccept()),
				    result -> {
				    	loadData();
				    }
			);
		}
		if (message instanceof RemoveFriendMessage) {
			RemoveFriendMessage removeFriendMessage = (RemoveFriendMessage) message;
			WebManager.enqueue(
					() -> API.social().deleteFriend(API.authKey, removeFriendMessage.getFriendName()),
				    result -> {
				    	loadData();
				    }
			);
		}
		if (message instanceof CreateGroupMessage) {
			WebManager.enqueue(
					() -> API.social().createGroup(API.authKey),
				    result -> {
				    	loadData();
				    }
			);
		}
		if (message instanceof LeaveGroupMessage) {
			WebManager.enqueue(
					() -> API.social().leaveGroup(API.authKey),
				    result -> {
				    	loadData();
				    }
			);
		}
		if (message instanceof FriendRequestMessage) {
			FriendRequestMessage friendRequestMessage = (FriendRequestMessage) message;
			WebManager.enqueue(
					() -> API.social().sendFriendRequest(API.authKey, friendRequestMessage.getToName()),
				    result -> {
				    	SocialModel.friendRequestSendResponse = result;
				    }
			);
		}
		if (message instanceof InviteToGroupMessage) {
			InviteToGroupMessage inviteToGroupMessage = (InviteToGroupMessage) message;
			WebManager.enqueue(
					() -> API.social().inviteToGroup(API.authKey, inviteToGroupMessage.getFriendName()),
				    result -> {
				    	SocialModel.groupInviteSendResponse = result;
				    }
			);
		}
	}

	@Override
	public void close() {
		
	}

}
