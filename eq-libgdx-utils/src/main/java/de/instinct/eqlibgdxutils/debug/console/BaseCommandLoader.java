package de.instinct.eqlibgdxutils.debug.console;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class BaseCommandLoader implements CommandLoader {

	@Override
	public List<Command> getCommands() {
		List<Command> baseCommands = new ArrayList<>();
		baseCommands.add(Command.builder()
				.method("filter")
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
				.description("clears the consoles tag blacklist")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						Console.getTagFilter().clear();
						Logger.log("Command", "tag filters cleared");
					}
					
				})
				.build());
		baseCommands.add(Command.builder()
				.method("addfilter=")
				.description("add tag to the consoles tag blacklist")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String tag = message.replace("addfilter=", "");
						Console.getTagFilter().add(tag);
						Logger.log("Command", "filter added: " + tag);
					}
					
				})
				.build());
		baseCommands.add(Command.builder()
				.method("removefilter=")
				.description("remove tag from the consoles tag blacklist")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String tag = message.replace("removefilter=", "");
						Console.getTagFilter().remove(tag);
						Logger.log("Command", "filter removed: " + tag);
					}
					
				})
				.build());
		baseCommands.add(Command.builder()
				.method("loadfilter")
				.description("loads the consoles tag blacklist from preferences")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String filterString = PreferenceUtil.load("console_tag_filter");
						for (String tag : filterString.split(";")) {
							if (!tag.isEmpty()) {
								Console.getTagFilter().add(tag);
							}
						}
						Logger.log("Command", "filters loaded from preferences");
					}
					
				})
				.build());
		baseCommands.add(Command.builder()
				.method("savefilter")
				.description("saves the consoles tag blacklist to preferences")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String filterString = "";
						for (String tag : Console.getTagFilter()) {
							if (!filterString.isEmpty()) {
								filterString += ";";
							}
							filterString += tag;
						}
						PreferenceUtil.save("console_tag_filter", filterString);
						Logger.log("Command", "filters saved to preferences");
					}
					
				})
				.build());
		return baseCommands;
	}

}
