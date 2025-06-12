package de.instinct.eqlibgdxutils.debug.logging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.badlogic.gdx.Gdx;

public class Logger {
	
	private static List<LogLine> logs = new ArrayList<>();
	private static final Object logsLock = new Object();

	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

	public static void log(String message) {
		log("UNTAGGED", message);
	}

	public static void log(String tag, String message) {
		log(tag, message, ConsoleColor.DEFAULT);
	}
	
	public static void clear() {
		synchronized(logsLock) {
	        logs.clear();
	    }
	}
	
	public static void log(String tag, String message, ConsoleColor color) {
		long timestamp = System.currentTimeMillis();
		String formattedTime = formatter.format(new Date(timestamp));
		System.out.print(color.getCode());
		Gdx.app.log(formattedTime, tag + " - " + message);
		System.out.print(ConsoleColor.DEFAULT.getCode());
		synchronized(logsLock) {
		    logs.add(LogLine.builder()
		            .timestamp(timestamp)
		            .timestampString(formattedTime)
		            .color(color)
		            .message(message)
		            .tag(tag)
		            .build());
		}
	}
	
	public static List<LogLine> getLogs() {
	    synchronized(logsLock) {
	        return Collections.unmodifiableList(new ArrayList<>(logs));
	    }
	}
	
	public static List<LogLine> getLogs(int lastN) {
	    synchronized(logsLock) {
	        return Collections.unmodifiableList(new ArrayList<>(logs).subList(logs.size() > lastN ? logs.size() - lastN : 0, logs.size()));
	    }
	}
	
	public static int logCount() {
	    synchronized(logsLock) {
	        return logs.size();
	    }
	}
	
}