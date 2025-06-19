package de.instinct.eqlibgdxutils.debug.console;

import de.instinct.eqlibgdxutils.generic.Action;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Command {
	
	private String method;
	private Action action;

}
