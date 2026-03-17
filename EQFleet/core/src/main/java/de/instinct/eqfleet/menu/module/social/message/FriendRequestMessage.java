package de.instinct.eqfleet.menu.module.social.message;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class FriendRequestMessage extends SocialMessage {
	
	private String toName;

}
