package de.instinct.eqfleetshared.net.enums;

public enum FactionMode {
	
	ONE_VS_ONE(1),
	TWO_VS_TWO(2),
	THREE_VS_THREE(3);
	
	public int teamPlayerCount;
	
	private FactionMode(int teamPlayerCount) {
		this.teamPlayerCount = teamPlayerCount;
	}

}
