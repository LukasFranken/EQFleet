package de.instinct.engine.model.data;

import java.util.Map;

import lombok.ToString;

@ToString(callSuper = true)
public class PauseData {
	
	public long resumeCountdownMS;
	public long currentPauseElapsedMS;
	public Map<Integer, Long> teamPausesMS;
	public Map<Integer, Integer> teamPausesCount;
	public int teamPause;
	public String currentPauseReason;

}
