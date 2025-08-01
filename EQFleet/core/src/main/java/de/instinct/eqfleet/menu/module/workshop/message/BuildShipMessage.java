package de.instinct.eqfleet.menu.module.workshop.message;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class BuildShipMessage extends ModuleMessage {
	
	private String shipUUID;

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.WORKSHOP;
	}
	
}
