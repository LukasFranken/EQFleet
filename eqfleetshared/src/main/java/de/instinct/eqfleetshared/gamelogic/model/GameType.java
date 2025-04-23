package de.instinct.eqfleetshared.gamelogic.model;

import de.instinct.eqfleetshared.net.enums.FactionMode;
import de.instinct.eqfleetshared.net.enums.GameMode;
import de.instinct.eqfleetshared.net.enums.VersusMode;

public class GameType {
	
	public VersusMode versusMode;
	public GameMode gameMode;
	public FactionMode factionMode;
	
	public boolean equals(GameType otherGameType) {
		if (versusMode != otherGameType.versusMode) return false;
		if (gameMode != otherGameType.gameMode) return false;
		if (factionMode != otherGameType.factionMode) return false;
		return true;
	}
	
	public String toString() {
		return "GameType=[VersusMode: " + versusMode.toString() + ". GameMode: " + gameMode.toString() + ". FactionMode: " + factionMode.toString();
	}

}
