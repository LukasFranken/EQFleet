package de.instinct.eqlibgdxutils.debug.console;

import java.util.ArrayList;
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
		if (message.toLowerCase().startsWith("help")) {
			printBaseHelp();
			return;
		} else {
			for (Command command : commands) {
				if (message.toLowerCase().startsWith(command.getMethod())) {
					command.getAction().execute(message);
					return;
				}
			}
		}
		Logger.log("Console", "Command not found: " + message);
	}

	private void printBaseHelp() {
		Logger.log("Console", "-------Console Help--------");
		Logger.log("Console", "Use listall to list all commands");
		Logger.log("Console", "Use list=<tag> to list all commands starting with that tag");
		Logger.log("Console", "---------------------------");
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
	
	public void addCommands(List<Command> newCommands) {
		if (commands == null) commands = new ArrayList<>();
		this.commands.addAll(newCommands);
	}

}
