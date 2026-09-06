package de.instinct.eqlibgdxutils.platform.preference;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferenceStore {
	
	private final Preferences preferences;
	
	public PreferenceStore(String storeName) {
		this.preferences = Objects.requireNonNull(openStore(storeName), "preferences");
	}
	
	private static Preferences openStore(String storeName) {
		Objects.requireNonNull(storeName, "storeName");
		if (storeName.trim().isEmpty()) {
			throw new IllegalArgumentException("storeName must not be blank");
		}
		if (Gdx.app == null) {
			throw new IllegalStateException("Create PreferenceManager after libGDX application startup");
		}
		return Gdx.app.getPreferences(storeName);
	}
	
	public String load(String key) {
		return load(key, "");
	}

	public String load(String key, String defaultValue) {
		Objects.requireNonNull(key, "key");
		Object value = preferences.get().get(key);
		return value == null ? defaultValue : String.valueOf(value);
	}

	public boolean loadBoolean(String key, boolean defaultValue) {
		String value = load(key, "").trim();
		if ("true".equalsIgnoreCase(value)) return true;
		if ("false".equalsIgnoreCase(value)) return false;
		return defaultValue;
	}

	public int loadInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(load(key, "").trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public long loadLong(String key, long defaultValue) {
		try {
			return Long.parseLong(load(key, "").trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public float loadFloat(String key, float defaultValue) {
		try {
			float value = Float.parseFloat(load(key, "").trim());
			return Float.isNaN(value) || Float.isInfinite(value) ? defaultValue : value;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public void save(String key, String value) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(value, "value");
		preferences.putString(key, value);
		preferences.flush();
	}

	public void save(String key, boolean value) {
		save(key, Boolean.toString(value));
	}

	public void save(String key, int value) {
		save(key, Integer.toString(value));
	}

	public void save(String key, long value) {
		save(key, Long.toString(value));
	}

	public void save(String key, float value) {
		if (Float.isNaN(value) || Float.isInfinite(value)) {
			throw new IllegalArgumentException("value must be finite");
		}
		save(key, Float.toString(value));
	}

	public boolean contains(String key) {
		return preferences.contains(Objects.requireNonNull(key, "key"));
	}

	public Map<String, ?> getAll() {
		return Collections.unmodifiableMap(new LinkedHashMap<String, Object>(preferences.get()));
	}

	public Set<String> keys() {
		return getAll().keySet();
	}

	public boolean delete(String key) {
		if (!contains(key)) return false;
		preferences.remove(key);
		preferences.flush();
		return true;
	}

	public void deleteAll() {
		preferences.clear();
		preferences.flush();
	}

}
