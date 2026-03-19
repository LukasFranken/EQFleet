package de.instinct.eqfleet.menu.module.social;

import java.util.Queue;

import de.instinct.api.social.dto.FriendRequestSendResponse;
import de.instinct.api.social.dto.Group;
import de.instinct.api.social.dto.GroupInviteResponse;
import de.instinct.api.social.dto.PlayerSocialData;
import de.instinct.eqfleet.menu.module.social.message.SocialMessage;

public class SocialModel {
	
	public static volatile Queue<SocialMessage> messageQueue;
	
	public static volatile PlayerSocialData playerData;
	public static volatile Group groupData;
	public static volatile FriendRequestSendResponse friendRequestSendResponse;
	public static volatile GroupInviteResponse groupInviteSendResponse;

}
