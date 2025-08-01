package de.instinct.eqfleet.menu.module.construction.message;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class UseTurretMessage extends ModuleMessage {

	private String turretUUID;
	
	@Override
	public MenuModule getMenuModule() {
		return MenuModule.CONSTRUCTION;
	}
}
