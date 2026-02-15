package de.instinct.eqlibgdxutils.debug.profiler.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackedCheckpoint {

	private String sectionTag;
	private String tag;
	
}
