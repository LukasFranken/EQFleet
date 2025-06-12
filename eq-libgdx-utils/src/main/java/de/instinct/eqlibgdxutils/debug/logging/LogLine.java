package de.instinct.eqlibgdxutils.debug.logging;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogLine {
	
	private ConsoleColor color;
	private long timestamp;
	private String timestampString;
	private String message;
	private String tag;

}
