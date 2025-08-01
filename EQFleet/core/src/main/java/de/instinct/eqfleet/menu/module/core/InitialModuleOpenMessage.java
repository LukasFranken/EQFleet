package de.instinct.eqfleet.menu.module.core;

import de.instinct.api.core.modules.MenuModule;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitialModuleOpenMessage {
	
	private MenuModule module;
	private String message;
	private boolean opened;

}
