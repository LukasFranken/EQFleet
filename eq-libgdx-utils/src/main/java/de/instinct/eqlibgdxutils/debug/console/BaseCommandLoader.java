package de.instinct.eqlibgdxutils.debug.console;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class BaseCommandLoader implements CommandLoader {

	@Override
	public List<Command> getCommands() {
		List<Command> baseCommands = new ArrayList<>();
		baseCommands.add(Command.builder()
				.method("clear")
				.logMethod("clear")
				.description("clear the console log")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						Logger.clear();
					}
				})
				.build());
		baseCommands.add(Command.builder()
				.method("listall")
				.logMethod("listall")
				.description("list all commands")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						for (Command command : Console.getRegisteredCommands()) {
							Logger.log("Console", command.getLogMethod() + " - " + command.getDescription());
						}
					}
					
				})
				.build());
		baseCommands.add(Command.builder()
				.method("list=")
				.logMethod("list=<key>")
				.description("list all commands that start with the given key")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String commandStart = message.replace("list=", "");
						for (Command command : Console.getRegisteredCommands()) {
							if (command.getMethod().startsWith(commandStart.toLowerCase())) {
								Logger.log("Console", command.getLogMethod() + " - " + command.getDescription());
							}
						}
					}
					
				})
				.build());
		
		baseCommands.add(Command.builder()
				.method("filter")
				.logMethod("filter")
				.description("show consoles tag blacklist")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						Logger.log("Command", "tag filters:");
						for (String tag : Console.getTagFilter()) {
							Logger.log("Command", tag);
						}
					}
					
				})
				.build());
		baseCommands.add(Command.builder()
				.method("clearfilter")
				.logMethod("clearfilter")
				.description("clears the consoles tag blacklist")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						Console.getTagFilter().clear();
						Console.saveFilter();
						Logger.log("Command", "tag filters cleared");
					}
					
				})
				.build());
		baseCommands.add(Command.builder()
				.method("addfilter=")
				.logMethod("addfilter=<tag>")
				.description("add tag to the consoles tag blacklist")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String tag = message.replace("addfilter=", "");
						Console.getTagFilter().add(tag);
						Console.saveFilter();
						Logger.log("Command", "filter added: " + tag);
					}
					
				})
				.build());
		baseCommands.add(Command.builder()
				.method("removefilter=")
				.logMethod("removefilter=<tag>")
				.description("remove tag from the consoles tag blacklist")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String tag = message.replace("removefilter=", "");
						Console.getTagFilter().remove(tag);
						Console.saveFilter();
						Logger.log("Command", "filter removed: " + tag);
					}
					
				})
				.build());
		return baseCommands;
	}

}
