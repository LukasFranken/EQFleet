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
	
	public static void log(String tag, Exception e) {
		log(tag, e.getMessage(), ConsoleColor.RED);
		for (StackTraceElement element : e.getStackTrace()) {
			String classname = element.getFileName() == null ? "null" : (element.getFileName().contains(".") ? element.getFileName().split("\\.")[0] : element.getFileName());
			log(tag, classname + "." + element.getMethodName() + ":" + element.getLineNumber(), ConsoleColor.RED);
		}
	}

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
	
	public static List<LogLine> getLogs(int lastN, List<String> tagBlacklist) {
		ArrayList<LogLine> tagSortedLogs = new ArrayList<>();
		synchronized(logsLock) {
	        for (LogLine log : logs) {
	            if (!tagBlacklist.contains(log.getTag())) {
	                tagSortedLogs.add(log);
	            }
	        }
	    }
	    synchronized(logsLock) {
	        return Collections.unmodifiableList(new ArrayList<>(tagSortedLogs).subList(tagSortedLogs.size() > lastN ? tagSortedLogs.size() - lastN : 0, tagSortedLogs.size()));
	    }
	}
	
	public static List<String> getTags() {
	    synchronized(logsLock) {
	        List<String> tags = new ArrayList<>();
	        for (LogLine log : logs) {
	            if (!tags.contains(log.getTag())) {
	                tags.add(log.getTag());
	            }
	        }
	        return Collections.unmodifiableList(tags);
	    }
	}
	
	public static int logCount() {
	    synchronized(logsLock) {
	        return logs.size();
	    }
	}
	
}