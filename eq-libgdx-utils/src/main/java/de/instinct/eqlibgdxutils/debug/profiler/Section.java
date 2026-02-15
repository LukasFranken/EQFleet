package de.instinct.eqlibgdxutils.debug.profiler;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Section {

	private String tag;
	private long maxFrameTimeNS;
	private Frame maxFrame;
	private long minFrameTimeNS;
	private Frame minFrame;
	private Frame avgFrame;
	private List<Frame> frames;
	
}
