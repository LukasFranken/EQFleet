package de.instinct.eqlibgdxutils.debug.logging.service;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatter {
	
	private final SimpleDateFormat fullFormatter = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
	private final SimpleDateFormat timeOnlyFormatter = new SimpleDateFormat("HH:mm:ss");
	private final SimpleDateFormat dateOnlyFormatter = new SimpleDateFormat("dd.MM.yyyy");
	private final SimpleDateFormat minutesFormatter = new SimpleDateFormat("mm:ss");
	private final SimpleDateFormat millisecondsFormatter = new SimpleDateFormat("mm:ss:SSS");
	
	public String format(long timestamp, LoggingTimeFormat format) {
		switch (format) {
			case FULL:
				return fullFormatter.format(new Date(timestamp));
			case TIME_ONLY:
				return timeOnlyFormatter.format(new Date(timestamp));
			case DATE_ONLY:
				return dateOnlyFormatter.format(new Date(timestamp));
			case MINUTES:
				return minutesFormatter.format(new Date(timestamp));
			case MILLISECONDS:
				return millisecondsFormatter.format(new Date(timestamp));
			case NONE:
				return "";
			default:
				return "";
		}
	}

}
