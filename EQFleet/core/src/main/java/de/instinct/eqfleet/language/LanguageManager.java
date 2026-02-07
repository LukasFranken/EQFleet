package de.instinct.eqfleet.language;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import de.instinct.eqfleet.PreferenceManager;
import de.instinct.eqfleet.language.model.Language;
import de.instinct.eqfleet.language.model.LanguageData;
import de.instinct.eqfleet.language.model.Translation;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.net.ObjectJSONMapper;

public class LanguageManager {

	private static final String LOGTAG = "LANGUAGE";

	private static Language currentLanguage;
	private static LanguageData languageData;

	public static void init() {
		loadLanguages();
	}

	private static void loadLanguages() {
		FileHandle fh = Gdx.files.internal("languagedata.json");
		languageData = ObjectJSONMapper.mapJSON(new String(fh.readBytes(), StandardCharsets.UTF_8), LanguageData.class);
		String languageCode = PreferenceManager.load("language");
		if (!languageCode.isEmpty()) {
			setLanguage(languageCode);
			Logger.log(LOGTAG, "Language set to " + languageCode, ConsoleColor.YELLOW);
		}
		if (currentLanguage == null)
			Logger.log(LOGTAG, "WARNING: No language set, defaulting to en", ConsoleColor.YELLOW);
			setLanguage("en");
	}

	public static void setLanguage(String code) {
		for (Language language : languageData.getAvailableLanguages()) {
			if (language.getCode().equals(code)) {
				PreferenceManager.save("language", code);
				currentLanguage = language;
				return;
			}
		}
	}

	public static Language getCurrentLanguage() {
		return currentLanguage;
	}
	
	public static List<Language> getAvailableLanguages() {
		return languageData.getAvailableLanguages();
	}

	public static String get(String key) {
		if (currentLanguage == null) {
			Logger.log(LOGTAG, "WARNING: No language set, returning key as value", ConsoleColor.RED);
			return key;
		}
		String value = null;
		for (Translation translation : languageData.getTranslations()) {
			if (translation.getKey().equals(key)) {
				value = translation.getTranslations().get(currentLanguage.getCode());
				break;
			}
		}
		if (value == null) {
			Logger.log(LOGTAG, "WARNING: Missing translation for: " + key + " for language " + currentLanguage.getCode(), ConsoleColor.RED);
			return key;
		}
		return value;
	}

	public static boolean languageIsSet() {
		return !PreferenceManager.load("language").isEmpty();
	}

}
