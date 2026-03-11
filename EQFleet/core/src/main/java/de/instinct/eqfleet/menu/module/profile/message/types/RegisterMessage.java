package de.instinct.eqfleet.menu.module.profile.message.types;

import de.instinct.eqfleet.menu.module.profile.message.ProfileMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class RegisterMessage extends ProfileMessage {
	
	private String username;

}
