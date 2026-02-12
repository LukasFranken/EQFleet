package de.instinct.engine.model.data;

import lombok.ToString;

@ToString
public class StaticData {

	public long maxGameTimeMS;
	public float zoomFactor;
	public double atpToWin;
	public float ancientPlanetResourceDegradationFactor;
	
	public long maxPauseMS;
	public long minPauseMS;
	
	public PlayerData playerData;
	
}
