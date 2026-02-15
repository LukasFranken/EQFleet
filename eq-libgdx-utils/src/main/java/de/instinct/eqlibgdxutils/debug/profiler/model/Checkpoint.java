package de.instinct.eqlibgdxutils.debug.profiler.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Checkpoint {
	
	private String tag;
	private long timeStampNS;
	private long durationNS;
	
	private long maxDurationNS;
	private long minDurationNS;
	private long avgDurationNS;
	private long totalDurationNS;
	
	private int frames;

}
