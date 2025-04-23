package de.instinct.eqlibgdxutils.debug.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Gdx;

public class Logger {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

	public static void log(String message) {
		log("UNTAGGED", message);
	}

	public static void log(String tag, String message) {
		String formattedTime = formatter.format(new Date(System.currentTimeMillis()));
		Gdx.app.log(formattedTime, tag + " - " + message);
	}
}