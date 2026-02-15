package de.instinct.eqlibgdxutils.debug.profiler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Checkpoint {
	
	private String tag;
	private long timeStampNS;
	private long durationNS;
	private int frames;

}
