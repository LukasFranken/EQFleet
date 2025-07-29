package de.instinct.engine.ai.difficulty;

import de.instinct.engine.model.AiPlayer;

public class DifficultyLoader {

	@SuppressWarnings("incomplete-switch")
	public void load(AiPlayer aiPlayer, int threatLevel) {
		AiDifficulty difficulty = AiDifficulty.RETARDED;
		if (threatLevel >= 100) {
			difficulty = AiDifficulty.EASY;
		}
		if (threatLevel >= 100) {
			difficulty = AiDifficulty.NORMAL;
		}
		AiBehaviorParameters behaviorParameters = new AiBehaviorParameters();
		switch (difficulty) {
		case RETARDED:
			behaviorParameters.defensiveShipDistanceThreshold = 300f;
			break;
		case EASY:
			behaviorParameters.defensiveShipDistanceThreshold = aiPlayer.planetData.weapon.range;
			break;
		case NORMAL:
			behaviorParameters.defensiveShipDistanceThreshold = aiPlayer.planetData.weapon.range;
			break;
		}
		aiPlayer.difficulty = difficulty;
		aiPlayer.behaviorParameters = behaviorParameters;
	}

}
