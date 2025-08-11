package de.instinct.eqlibgdxutils.debug.console;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Command {
	
	private String method;
	private String logMethod;
	private String description;
	private CommandAction action;

}
