package de.instinct.eqlibgdxutils.platform.preference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import de.instinct.eqlibgdxutils.debug.console.Command;
import de.instinct.eqlibgdxutils.debug.console.CommandAction;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class PreferenceConsoleRegistry {
	
	private static final String LOGTAG = "PREFS";
	private static final String consoleCommandPrefix = "pref";

	private PreferenceStore store;
	
	public PreferenceConsoleRegistry(PreferenceStore store) {
		this.store = store;
	}
	
	public void registerConsoleCommands() {
		List<Command> commands = createConsoleCommands(consoleCommandPrefix);
		List<Command> existing;
		try {
			existing = Console.getRegisteredCommands();
		} catch (NullPointerException e) {
			throw new IllegalStateException("Call Console.init() before registering preference commands", e);
		}
		if (existing != null) {
			for (Command added : commands) {
				for (Command registered : existing) {
					String method = registered.getMethod().toLowerCase(Locale.ROOT);
					if (method.startsWith(added.getMethod()) || added.getMethod().startsWith(method)) {
						throw new IllegalStateException("Console command conflict: " + registered.getMethod()
								+ "; choose another preference prefix");
					}
				}
			}
		}
		Console.addCommands(commands);
		log("Registered preference commands: " + consoleCommandPrefix);
	}

	private List<Command> createConsoleCommands(String prefix) {
		Objects.requireNonNull(prefix, "prefix");
		if (!prefix.matches("[a-z][a-z0-9_-]*") || prefix.startsWith("help")) {
			throw new IllegalArgumentException("prefix must be a lowercase identifier not starting with help");
		}
		String base = prefix + ".";
		List<Command> commands = new ArrayList<>();
		commands.add(command(base + "all", base + "all", "list all preferences", message -> {
			if (!message.equalsIgnoreCase(base + "all")) { usage(base + "all"); return; }
			log(String.join(",", store.keys()));
		}));
		commands.add(command(base + "load=", base + "load=<key>", "load the preference with the given key", message -> {
			String key = message.substring((base + "load=").length()).trim();
			if (key.isEmpty()) { usage(base + "load=<key>"); return; }
			log("Loaded preference: " + key + " = " + store.load(key));
		}));
		commands.add(command(base + "save=", base + "save=<key>,<value>", "save the preference with the given key", message -> {
			String argument = message.substring((base + "save=").length());
			int separator = argument.indexOf(',');
			if (separator < 0 || argument.substring(0, separator).trim().isEmpty()) {
				usage(base + "save=<key>,<value>"); return;
			}
			String key = argument.substring(0, separator).trim();
			String value = argument.substring(separator + 1).trim();
			store.save(key, value);
			log("Saved preference: " + key + " = " + value);
		}));
		commands.add(command(base + "deleteall", base + "deleteall", "delete all preferences", message -> {
			if (!message.equalsIgnoreCase(base + "deleteall")) { usage(base + "deleteall"); return; }
			Set<String> deleted = store.keys();
			store.deleteAll();
			for (String key : deleted) log("Deleted preference: " + key);
		}));
		commands.add(command(base + "delete=", base + "delete=<key>", "delete the preference with the given key", message -> {
			String key = message.substring((base + "delete=").length()).trim();
			if (key.isEmpty()) { usage(base + "delete=<key>"); return; }
			log(store.delete(key) ? "Deleted preference: " + key : "Preference '" + key + "' does not exist");
		}));
		return Collections.unmodifiableList(commands);
	}

	private Command command(String method, String syntax, String description, CommandAction action) {
		return Command.builder().method(method).logMethod(syntax).description(description).action(action).build();
	}

	private void usage(String syntax) {
		log("Usage: " + syntax);
	}

	private void log(String message) {
		Logger.log(LOGTAG, message, ConsoleColor.YELLOW);
	}

}
