package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferenceUtil {
	
	private static Preferences prefs;
	
	public static void init(String preferencesFolder) {
		prefs = Gdx.app.getPreferences(preferencesFolder);
	}

	public static void save(String key, String value) {
	    prefs.putString(key, value);
	    prefs.flush();
	}
	
	public static String load(String key) {
	    return prefs.getString(key, "");
	}
	
	public static void delete(String key) {
		prefs.remove(key);
		prefs.flush();
	}

}
