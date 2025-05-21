package de.instinct.eqfleet.menu.module.profile.message;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.main.ModuleMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class RegisterMessage extends ModuleMessage {
	
	@Override
	public MenuModule getMenuModule() {
		return MenuModule.PROFILE;
	}
	
	private String username;

}
