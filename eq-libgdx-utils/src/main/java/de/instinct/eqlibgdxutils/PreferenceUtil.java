package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class PreferenceUtil {
	
	private static final String LOGTAG = "PREFS";
	
	private static Preferences prefs;
	
	public static void init(String preferencesFolder) {
		prefs = Gdx.app.getPreferences(preferencesFolder);
		Logger.log(LOGTAG, "initialized for folder: " + preferencesFolder, ConsoleColor.YELLOW);
	}

	public static void save(String key, String value) {
	    prefs.putString(key, value);
	    prefs.flush();
	    Logger.log(LOGTAG, "Saved preference: " + key + " = " + value, ConsoleColor.YELLOW);
	}
	
	public static String load(String key) {
		String value = prefs.getString(key, "");
		Logger.log(LOGTAG, "Loaded preference: " + key + " = " + value, ConsoleColor.YELLOW);
	    return value;
	    
	}
	
	public static void delete(String key) {
		prefs.remove(key);
		prefs.flush();
		Logger.log(LOGTAG, "Deleted preference: " + key, ConsoleColor.YELLOW);
	}

}
