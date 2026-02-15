package de.instinct.eqlibgdxutils.debug.profiler.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Frame {
	
	private long startTimeNS;
	private long durationNS;
	private long totalDurationNS;
	private List<Checkpoint> checkpoints;

}
