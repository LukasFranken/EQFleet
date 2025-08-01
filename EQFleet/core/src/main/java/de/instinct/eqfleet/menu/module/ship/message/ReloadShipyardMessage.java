package de.instinct.eqfleet.menu.module.ship.message;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class ReloadShipyardMessage extends ModuleMessage {
	
	@Override
	public MenuModule getMenuModule() {
		return MenuModule.SHIPYARD;
	}

}
