package de.instinct.eqlibgdxutils.debug.logging.config;

import de.instinct.eqlibgdxutils.debug.logging.service.LoggingTimeFormat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoggingConfiguration {
	
	private LoggingTimeFormat timeFormat;

}
