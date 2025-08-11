package de.instinct.eqfleet;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.debug.console.Command;
import de.instinct.eqlibgdxutils.debug.console.CommandAction;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class PreferenceManager {
	
	private static final String LOGTAG = "PREFS";
	private static final String PREFERENCE_KEYS_REGISTRY = "preferencekeys";
	
	public static void init() {
		String preferencesFolder = "EQFleet";
		PreferenceUtil.init(preferencesFolder);
		loadPrefCommands();
		Logger.log(LOGTAG, "initialized for folder: " + preferencesFolder, ConsoleColor.YELLOW);
	}
	
	private static void loadPrefCommands() {
		List<Command> prefCommands = new ArrayList<>();
		prefCommands.add(Command.builder()
				.method("pref.all")
				.logMethod("pref.all")
				.description("list all preferences")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						Logger.log(LOGTAG, PreferenceUtil.load(PREFERENCE_KEYS_REGISTRY), ConsoleColor.YELLOW);
					}
					
				})
				.build());
		prefCommands.add(Command.builder()
				.method("pref.load=")
				.logMethod("pref.load=<key>")
				.description("load the preference with the given key")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String key = message.replace("pref.load=", "").trim();
						load(key);
					}
					
				})
				.build());
		prefCommands.add(Command.builder()
				.method("pref.save=")
				.logMethod("pref.save=<key>,<value>")
				.description("save the preference with the given key")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String key = message.replace("pref.save=", "").split(",")[0].trim();
						String value = message.replace("pref.save=", "").split(",")[1].trim();
						save(key, value);
					}
					
				})
				.build());
		prefCommands.add(Command.builder()
				.method("pref.deleteall")
				.logMethod("pref.deleteall")
				.description("delete all preferences")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						deleteAll();
					}
					
				})
				.build());
		prefCommands.add(Command.builder()
				.method("pref.delete=")
				.logMethod("pref.delete=<key>")
				.description("delete the preference with the given key")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String key = message.replace("pref.delete=", "").trim();
						delete(key);
					}
					
				})
				.build());
		Console.addCommands(prefCommands);
	}

	public static String load(String key) {
		String value = PreferenceUtil.load(key);
		Logger.log(LOGTAG, "Loaded preference: " + key + " = " + value, ConsoleColor.YELLOW);
		return value;
	}
	
	public static void save(String key, String value) {
		PreferenceUtil.save(key, value);
		addPreferenceToRegistry(key);
		Logger.log(LOGTAG, "Saved preference: " + key + " = " + value, ConsoleColor.YELLOW);
	}
	
	private static void addPreferenceToRegistry(String key) {
		String registry = PreferenceUtil.load(PREFERENCE_KEYS_REGISTRY);
		if (!key.contentEquals(PREFERENCE_KEYS_REGISTRY)) {
			if (registry.isEmpty()) {
				PreferenceUtil.save(PREFERENCE_KEYS_REGISTRY, key);
			} else {
				if (!registry.contains(key)) {
					PreferenceUtil.save(PREFERENCE_KEYS_REGISTRY, registry + "," + key);
				}
			}
		}
	}

	public static void delete(String key) {
		String registry = PreferenceUtil.load(PREFERENCE_KEYS_REGISTRY);
		PreferenceUtil.delete(key);
		String newRegistry = "";
		for (String prefKey : registry.split(",")) {
			if (newRegistry.isEmpty() && !prefKey.equals(key)) {
				newRegistry = prefKey;
			} else if (!prefKey.equals(key)) {
				newRegistry += "," + prefKey;
			}
		}
		PreferenceUtil.save(PREFERENCE_KEYS_REGISTRY, newRegistry);
		Logger.log(LOGTAG, "Deleted preference: " + key, ConsoleColor.YELLOW);
	}
	
	public static void deleteAll() {
		for (String key : PreferenceUtil.load(PREFERENCE_KEYS_REGISTRY).split(",")) {
			delete(key);
		}
	}

}
