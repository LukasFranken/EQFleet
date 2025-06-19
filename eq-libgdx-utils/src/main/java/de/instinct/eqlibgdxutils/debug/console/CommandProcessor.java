package de.instinct.eqlibgdxutils.debug.console;

import java.util.List;

import de.instinct.eqlibgdxutils.debug.logging.Logger;
import lombok.Data;

@Data
public class CommandProcessor {
	
	private List<Command> commands;
	
	public void process(String message) {
		if (commands == null || commands.isEmpty()) {
			Logger.log("Console", "No commands registered");
			return;
		}
		for (Command command : commands) {
			if (command.getMethod().equalsIgnoreCase(message)) {
				command.getAction().execute();
				return;
			}
		}
		Logger.log("Console", "Command not found: " + message);
	}

	public String autocomplete(String message) {
		if (commands == null || commands.isEmpty()) {
			Logger.log("Console", "No commands registered");
		} else {
			for (Command command : commands) {
				if (command.getMethod().startsWith(message)) {
					return command.getMethod();
				}
			}
		}
		return message;
	}

}
