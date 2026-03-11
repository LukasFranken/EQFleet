package de.instinct.eqfleet.menu.main.message.types;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.main.message.MenuMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class OpenModuleMessage extends MenuMessage {
	
	private MenuModule module;

}
