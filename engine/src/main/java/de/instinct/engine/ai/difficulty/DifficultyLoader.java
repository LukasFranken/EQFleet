package de.instinct.engine.ai.difficulty;

import de.instinct.engine.ai.AiDifficulty;
import de.instinct.engine.model.AiPlayer;

public class DifficultyLoader {

	@SuppressWarnings("incomplete-switch")
	public void load(AiPlayer aiPlayer, AiDifficulty difficulty) {
		AiBehaviorParameters behaviorParameters = new AiBehaviorParameters();
		switch (difficulty) {
		case RETARDED:
			behaviorParameters.defensiveShipDistanceThreshold = 300f;
			break;
		}
		aiPlayer.difficulty = difficulty;
		aiPlayer.behaviorParameters = behaviorParameters;
	}
	
	

}
