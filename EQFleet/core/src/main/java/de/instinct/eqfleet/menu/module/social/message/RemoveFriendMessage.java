package de.instinct.eqfleet.menu.module.social.message;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class RemoveFriendMessage extends SocialMessage {
	
	private String friendName;

}
