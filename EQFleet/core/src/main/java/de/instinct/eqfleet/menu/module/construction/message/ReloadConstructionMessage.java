package de.instinct.eqfleet.menu.module.construction.message;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class ReloadConstructionMessage extends ModuleMessage {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.CONSTRUCTION;
	}
	
}
