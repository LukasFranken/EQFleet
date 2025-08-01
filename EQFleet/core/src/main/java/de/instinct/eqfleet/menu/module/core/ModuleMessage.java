package de.instinct.eqfleet.menu.module.core;

import de.instinct.api.core.modules.MenuModule;
import lombok.Data;

@Data
public abstract class ModuleMessage {
	
	public abstract MenuModule getMenuModule();

}
