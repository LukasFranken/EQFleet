package de.instinct.engine.core.meta.data;

import java.util.Map;

public class PauseData {
	
	public long resumeCountdownMS;
	public long currentPauseElapsedMS;
	public Map<Integer, Long> teamPausesMS;
	public Map<Integer, Integer> teamPausesCount;
	public int teamPause;
	public String currentPauseReason;
	public long maxPauseMS;
	public long minPauseMS;

}
