package de.instinct.eqlibgdxutils.platform.preference;

public final class Preferences {
	
	private static PreferenceStore store;
	private static PreferenceConsoleRegistry registry;
	
	public static void init(String storeName) {
		store = new PreferenceStore(storeName);
		registry = new PreferenceConsoleRegistry(store);
		registry.registerConsoleCommands();
	}
	
	public static String load(String key) {
		return store.load(key, "");
	}
	
	public static void save(String key, String value) {
		store.save(key, value);
	}
	
}
